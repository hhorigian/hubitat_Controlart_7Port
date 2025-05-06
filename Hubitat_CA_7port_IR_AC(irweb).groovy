/**
 *   ControlArt 7Port Driver - Versão Usando IR MolSmart Database. Versão para controles de AC. 
 *   You must create your remote control template, at http://ir.molsmart.com.br. Then you can import your remote control over by using just the sharing URL. 
 *
 *  Copyright 2025 VH 
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *
 *            --- Driver para 7Port - IR - para TV ---
 *              V.1.0   5/5/2025 - V1 para trazer os controles remotos prontos. 

 *
 */
metadata {
  definition (name: "ControlArt - 7port - IR - AC (irweb)", namespace: "TRATO", author: "VH", vid: "generic-contact") {
		capability "Actuator"
		capability "Sensor"
		capability "Temperature Measurement"
		capability "Thermostat"
  		capability "PushableButton"
    	capability "Initialize"       
      

		attribute "supportedThermostatFanModes", "JSON_OBJECT"
		attribute "supportedThermostatModes", "JSON_OBJECT"	  
		attribute "hysteresis", "NUMBER"
		attribute "lastCommandStatus", "string" // To track command delivery status
      

    command "GetRemoteDATA"
    command "cleanvars"
    command "AtualizaDados7Port"
	command "reconnect"
	command "refresh"
    command "checkConnection" // Added explicit connection check command      
      
      
  }
      
      


}

    import groovy.transform.Field
    import groovy.json.JsonOutput

    @Field static final String DRIVER = "by TRATO"
    @Field static final String USER_GUIDE = "https://github.com/hhorigian/hubitat_Controlart_7Port/tree/main"

// Connection parameters
@Field static final Integer RECONNECT_DELAY = 30 // seconds between reconnect attempts
@Field static final Integer CONNECTION_TIMEOUT = 10 // seconds for connection timeout
@Field static final Integer KEEPALIVE_INTERVAL = 300 // seconds between keepalive checks
@Field static final Integer MAX_RETRY_ATTEMPTS = 3 // max retry attempts for sending commands



    String fmtHelpInfo(String str) {
    String prefLink = "<a href='${USER_GUIDE}' target='_blank'>${str}<br><div style='font-size: 70%;'>${DRIVER}</div></a>"
    return "<div style='font-size: 160%; font-style: bold; padding: 2px 0px; text-align: center;'>${prefLink}</div>"
    }


    @Field static final String DRIVER1 = "IR MolSmart"
    @Field static final String USER_GUIDE1 = "https://ir.molsmart.com.br/"

    String fmtHelpInfo1(String str) {
    String prefLink1 = "<a href='${USER_GUIDE1}' target='_blank'>${str}<br><div style='font-size: 70%;'>${DRIVER1}</div></a>"
    return "<div style='font-size: 160%; font-style: bold; padding: 2px 0px; text-align: center;'>${prefLink1}</div>"
    }



  preferences {
    input name: "logEnable", type: "bool", title: "Enable debug logging", defaultValue: false
        input name: "molIPAddress", type: "text", title: "ControlArt IP Address", submitOnChange: true, required: true, defaultValue: "192.168.1.100" 
	    input name: "channel", title:"Canal Infravermelho (1-8)", type: "string", required: true        
        input name: "logEnable", type: "bool", title: "Enable debug logging", defaultValue: false
	    input "device_port", "number", title: "Port of 7port", required: true, defaultValue: 4998

        //help guide
        input name: "UserGuide", type: "hidden", title: fmtHelpInfo("Manual do Driver") 
        input name: "SiteIR", type: "hidden", title: fmtHelpInfo1("Site IR MolSmart") 
        input name: "webserviceurl", title:"URL Do Controle Remoto", type: "string"

  }   
  

def initialize() {
	log.debug "initialized()"  
    sendEvent(name:"numberOfButtons", value:60)  		
	if (state?.lastRunningMode == null) {
		sendEvent(name: "temperature", value: convertTemperatureIfNeeded(68.0,"F",1))
		sendEvent(name: "thermostatSetpoint", value: convertTemperatureIfNeeded(68.0,"F",1))
		sendEvent(name: "heatingSetpoint", value: convertTemperatureIfNeeded(68.0,"F",1))
		sendEvent(name: "coolingSetpoint", value: convertTemperatureIfNeeded(75.0,"F",1))
		state.lastRunningMode = "heat"
		updateDataValue("lastRunningMode", "heat")
		setThermostatOperatingState("idle")
		setSupportedThermostatFanModes(JsonOutput.toJson(["auto","high","mid","low"]))
		setSupportedThermostatModes(JsonOutput.toJson(["auto", "cool", "heat", "off"]))
		off()
		fanAuto()
	}
	sendEvent(name: "hysteresis", value: (hysteresis ?: 0.5).toBigDecimal())
    
    // Initialize connection state
    state.connectionAttempts = 0
    state.lastCommand = ""
    state.lastCommandAttempts = 0
    
    // Start connection process
    connectToDevice()
    
}


def socketStatus(status) {
    logDebug("Socket status: ${status}")
    sendEvent(name: "socketStatus", value: status)
    
    if (status.contains("error") || status.contains("Broken pipe")) {
        logWarn("Socket error detected, attempting to reconnect...")
        runIn(5, "reconnect") // Delay slightly before reconnecting
    }
}



def connectToDevice() {
    logDebug("Attempting to connect to device at ${molIPAddress}:${device_port}")
    
    // Close any existing connection
    try {
        interfaces.rawSocket.close()
    } catch (Exception e) {
        logDebug("Error closing socket: ${e.message}")
    }
    
    sendEvent(name: "connectionStatus", value: "connecting")
    sendEvent(name: "socketStatus", value: "connecting")
    
    try {
        interfaces.rawSocket.connect(molIPAddress, device_port.toInteger())
        state.lastConnectionAttempt = now()
        state.connectionAttempts = (state.connectionAttempts ?: 0) + 1
        
        logInfo("Successfully connected to device")
        sendEvent(name: "connectionStatus", value: "connected")
        sendEvent(name: "socketStatus", value: "connected")
        state.connectionAttempts = 0
        
        runIn(KEEPALIVE_INTERVAL, "checkConnection")
        
        if (state.lastCommand) {
            runIn(2, "sendPendingCommand")
        }
    } catch (Exception e) {
        handleConnectionError(e)
    }
}


def handleConnectionError(error) {
    def errorMsg = error.message ?: "Unknown error"
    logError("Connection failed: ${errorMsg}")
    sendEvent(name: "connectionStatus", value: "disconnected")
    sendEvent(name: "socketStatus", value: "error: ${errorMsg}")
    
    if (state.connectionAttempts < MAX_RETRY_ATTEMPTS) {
        def delay = RECONNECT_DELAY * (state.connectionAttempts ?: 1)
        logDebug("Will retry connection in ${delay} seconds")
        runIn(delay, "connectToDevice")
    } else {
        logWarn("Max connection attempts reached. Please check device settings.")
    }
}

def rawSocketStatus(status) {
    // This method will be called automatically by Hubitat when socket status changes
    socketStatus(status)
}



def checkConnection() {
    if (isConnected()) {
        logDebug("Connection check: Device is connected")
        sendEvent(name: "connectionStatus", value: "connected")
        
        // Send a simple command to verify the connection is alive
        try {
            interfaces.rawSocket.sendMessage("ping")
            runIn(KEEPALIVE_INTERVAL, "checkConnection")
        } catch (Exception e) {
            logError("Keepalive check failed: ${e.message}")
            sendEvent(name: "connectionStatus", value: "disconnected")
            reconnect()
        }
    } else {
        logDebug("Connection check: Device is not connected")
        sendEvent(name: "connectionStatus", value: "disconnected")
        reconnect()
    }
}

def isConnected() {
    try {
        // Simple check to see if socket is connected
        return interfaces.rawSocket.isConnected()
    } catch (Exception e) {
        return false
    }
}

def reconnect() {
    logDebug("Attempting to reconnect...")
    connectToDevice()
}

def sendPendingCommand() {
    if (state.lastCommand && state.lastCommandAttempts < MAX_RETRY_ATTEMPTS) {
        logDebug("Resending pending command: ${state.lastCommand}")
        state.lastCommandAttempts = (state.lastCommandAttempts ?: 0) + 1
        sendCommand(state.lastCommand)
    } else {
        logWarn("No pending command or max retries reached")
        state.lastCommand = ""
        state.lastCommandAttempts = 0
    }
}

private sendCommand(command) {
    if (!isConnected()) {
        logWarn("Cannot send command - not connected to device")
        state.lastCommand = command
        state.lastCommandAttempts = 0
        reconnect()
        return false
    }
    
    try {
        logDebug("Sending command: ${command}")
        interfaces.rawSocket.sendMessage(command)
        state.lastCommand = ""
        state.lastCommandAttempts = 0
        return true
    } catch (Exception e) {
        logError("Failed to send command: ${e.message}")
        socketStatus("send error: ${e.message}")
        state.lastCommand = command
        sendEvent(name: "connectionStatus", value: "disconnected")
        reconnect()
        return false
    }
}


//// ENVIA COMANDO ////


private EnviaComando(command) {
    logDebug("Attempting to send command: ${command}")
    def completesendir = "sendir,1:" + state.channel + ",1," + command
    
    if (!isConnected()) {
        logWarn("Not connected, queuing command for retry")
        queueCommandForRetry(completesendir)
        return false
    }
    
    try {
        // Store sent command for verification
        state.lastSentCommand = completesendir
        state.lastSentTime = now()
        state.waitingForResponse = true
        
        interfaces.rawSocket.sendMessage(completesendir)
        logDebug("Command sent, waiting for confirmation")
        sendEvent(name: "lastCommandStatus", value: "sent")
        
        // Set timeout to check for response
        runIn(5, "checkCommandResponse")
        return true
    } catch (Exception e) {
        logError("Failed to send command: ${e.message}")
        sendEvent(name: "lastCommandStatus", value: "failed: ${e.message}")
        queueCommandForRetry(completesendir)
        return false
    }
}

def queueCommandForRetry(command) {
    if (!state.queuedCommands) {
        state.queuedCommands = []
    }
    
    // Add command to queue if not already there
    if (!state.queuedCommands.contains(command)) {
        state.queuedCommands << command
        logDebug("Command added to queue (${state.queuedCommands.size()} pending)")
    }
    
    // Start retry process if not already running
    if (!state.retryInProgress) {
        state.retryInProgress = true
        runIn(10, "processQueuedCommands")
    }
}

def processQueuedCommands() {
    if (!isConnected()) {
        logDebug("Still not connected, will retry queued commands later")
        state.retryInProgress = false
        reconnect()
        return
    }
    
    def successfulSends = 0
    def failedSends = 0
    
    state.queuedCommands?.each { cmd ->
        try {
            interfaces.rawSocket.sendMessage(cmd)
            successfulSends++
            logDebug("Retried command successfully: ${cmd}")
        } catch (Exception e) {
            failedSends++
            logError("Retry failed for command: ${cmd} - ${e.message}")
        }
    }
    
    if (failedSends > 0) {
        logWarn("${failedSends} commands failed during retry")
        runIn(30, "processQueuedCommands") // Try again later
    } else {
        state.retryInProgress = false
        state.queuedCommands = []
        logInfo("All queued commands processed successfully")
    }
    
    sendEvent(name: "lastCommandStatus", 
             value: "retry results: ${successfulSends} success, ${failedSends} failed")
}

def checkCommandResponse() {
    if (state.waitingForResponse) {
        logWarn("No response received for last command")
        sendEvent(name: "lastCommandStatus", value: "timeout: no response")
        queueCommandForRetry(state.lastSentCommand)
        state.waitingForResponse = false
    }
}

def parse(msg) {
    state.lastMessageReceived = new Date(now()).toString()
    state.lastMessageReceivedAt = now()
        
    def newmsg = hubitat.helper.HexUtils.hexStringToByteArray(msg)
    def newmsg2 = new String(newmsg)
    
    state.lastmessage = newmsg2
    logDebug("Received message: ${newmsg2}")
    
    // Check if this is a response to our command
    if (state.waitingForResponse && newmsg2.contains("completeir")) {
        state.waitingForResponse = false
        unschedule("checkCommandResponse")
        sendEvent(name: "lastCommandStatus", value: "confirmed")
        logInfo("Command execution confirmed by device")
    }
    
    // Update connection status
    sendEvent(name: "connectionStatus", value: "connected")
    sendEvent(name: "socketStatus", value: "active")
    
    // Reset keepalive timer
    unschedule("checkConnection")
    runIn(KEEPALIVE_INTERVAL, "checkConnection")
}



//// Gets data fom IR MolSmart

def GetRemoteDATA()
{
  
    def params = [
        uri: webserviceurl,
        contentType: "application/json"
    ]
    try {
        httpGet(params) { resp ->
            if (resp.success) {                
                sendEvent(name: "GetRemoteData", value: "Sucess")
                //log.debug "RESULT = " + resp.data
      
sendEvent(name: "Controle", value: resp.data.name)   
sendEvent(name: "TipoControle", value: resp.data.type)   
sendEvent(name: "Formato", value: resp.data.conversor)  
log.info "Controle: " + resp.data.name
log.info "TipoControle: " +     resp.data.type    
                
state.encoding = resp.data.conversor			 		
state.poweroff = resp.data.functions.function[0]
state.poweron = resp.data.functions.function[1]
state.auto = resp.data.functions.function[2]
state.heat = resp.data.functions.function[3]
state.cool = resp.data.functions.function[4]
state.fan = resp.data.functions.function[5]
state.dry = resp.data.functions.function[6]
state.setautocool = resp.data.functions.function[7]
state.fanauto = resp.data.functions.function[8]
state.fanLow = resp.data.functions.function[9]
state.fanmed = resp.data.functions.function[10]
state.fanhigh = resp.data.functions.function[11]
state.comandoextra1 = resp.data.functions.function[12]
state.comandoextra2 = resp.data.functions.function[13]
state.comandoextra3 = resp.data.functions.function[14]
state.comandoextra4 = resp.data.functions.function[15]
state.comandoextra5 = resp.data.functions.function[16]
state.comandoextra6 = resp.data.functions.function[17]
state.comandoextra7 = resp.data.functions.function[18]
state.comandoextra8 = resp.data.functions.function[19]
state.fastcold = resp.data.functions.function[20]
state.temp18 = resp.data.functions.function[21]
state.temp20 = resp.data.functions.function[22]
state.temp22 = resp.data.functions.function[23]
state.clock = resp.data.functions.function[24]
state.sweep = resp.data.functions.function[25]
state.turbo = resp.data.functions.function[26]
state.fan = resp.data.functions.function[27]
state.temp17 = resp.data.functions.function[28]
state.temp23 = resp.data.functions.function[29]
state.temp26 = resp.data.functions.function[30]
state.onoff = resp.data.functions.function[31]
state.temp19 = resp.data.functions.function[32]
state.temp21 = resp.data.functions.function[33]
state.swing = resp.data.functions.function[34]
state.manual = resp.data.functions.function[35]
state.mode = resp.data.functions.function[36]
state.up = resp.data.functions.function[37]
state.timer = resp.data.functions.function[38]
state.cancel = resp.data.functions.function[39]
state.down = resp.data.functions.function[40]
state.display = resp.data.functions.function[41]
state.io = resp.data.functions.function[42]
state.tempup = resp.data.functions.function[43]
state.tempdown = resp.data.functions.function[44]
state.fanspeed = resp.data.functions.function[45]
         

    
    }
            
	}
    } catch (Exception e) {
        log.warn "Get Remote Control Info failed: ${e.message}"
    }    

}



def cleanvars()  //Usada para limpar todos os states e controles aprendidos. 
{
//state.remove()
  state.clear() 
  AtualizaDados7Port()  
}


def installed()
{
	log.warn "installed..."
	initialize()

}


def updated() {
    log.debug "updated()"
    initialize()
    AtualizaDados7Port()
    
    // Clear any pending commands when settings change
    state.queuedCommands = []
    state.retryInProgress = false
    state.waitingForResponse = false
}


//Get Device info and set as state to use during driver.
def AtualizaDados7Port() {
    state.currentip = settings.molIPAddress
    state.channel = settings.channel
    log.info "Dados do 7Port atualizados: " + state.currentip + " -- " + state.channel 

}


def refresh() {
    //def msg = "mdcmd_getmd," + state.newmacdec
    def msg = "sendir,1:8,1,38000,1,1,170,168,21,62,21,20,21,22,44,33"
    logTrace('Sent refresh()')   
    EnviaComando(msg)
}



def setSupportedThermostatFanModes(fanModes) {
	logDebug "setSupportedThermostatFanModes(${fanModes}) foi chamado"
	// (auto, circulate, on)
	sendEvent(name: "supportedThermostatFanModes", value: fanModes, descriptionText: getDescriptionText("supportedThermostatFanModes set to ${fanModes}"))
}

def setSupportedThermostatModes(modes) {
	logDebug "setSupportedThermostatModes(${modes}) foi chamado"
	// (auto, cool, emergency heat, heat, off)
	sendEvent(name: "supportedThermostatModes", value: modes, descriptionText: getDescriptionText("supportedThermostatModes set to ${modes}"))
}




def setThermostatOperatingState (operatingState) {
	logDebug "setThermostatOperatingState (${operatingState}) was called"
	updateSetpoints(null,null,null,operatingState)
	sendEvent(name: "thermostatOperatingState", value: operatingState, descriptionText: getDescriptionText("thermostatOperatingState set to ${operatingState}"))

}



private updateSetpoints(sp = null, hsp = null, csp = null, operatingState = null){
	if (operatingState in ["off"]) return
	if (hsp == null) hsp = device.currentValue("heatingSetpoint",true)
	if (csp == null) csp = device.currentValue("coolingSetpoint",true)
	if (sp == null) sp = device.currentValue("thermostatSetpoint",true)

	if (operatingState == null) operatingState = state.lastRunningMode

	def hspChange = isStateChange(device,"heatingSetpoint",hsp.toString())
	def cspChange = isStateChange(device,"coolingSetpoint",csp.toString())
	def spChange = isStateChange(device,"thermostatSetpoint",sp.toString())
	def osChange = operatingState != state.lastRunningMode

	def newOS
	def descriptionText
	def name
	def value
	def unit = "°${location.temperatureScale}"
	switch (operatingState) {
		case ["pending heat","heating","heat"]:
			newOS = "heat"
			if (spChange) {
				hspChange = true
				hsp = sp
			} else if (hspChange || osChange) {
				spChange = true
				sp = hsp
			}
			if (csp - 2 < hsp) {
				csp = hsp + 2
				cspChange = true
			}
			break
		case ["pending cool","cooling","cool"]:
			newOS = "cool"
			if (spChange) {
				cspChange = true
				csp = sp
			} else if (cspChange || osChange) {
				spChange = true
				sp = csp
			}
			if (hsp + 2 > csp) {
				hsp = csp - 2
				hspChange = true
			}
			break
		default :
			return
	}

	if (hspChange) {
		value = hsp
		name = "heatingSetpoint"
		descriptionText = "${device.displayName} ${name} was set to ${value}${unit}"
		if (txtEnable) log.info descriptionText
		sendEvent(name: name, value: value, descriptionText: descriptionText, unit: unit, stateChange: true)
	}
	if (cspChange) {
		value = csp
		name = "coolingSetpoint"
		descriptionText = "${device.displayName} ${name} was set to ${value}${unit}"
		if (txtEnable) log.info descriptionText
		sendEvent(name: name, value: value, descriptionText: descriptionText, unit: unit, stateChange: true)
	}
	if (spChange) {
		value = sp
		name = "thermostatSetpoint"
		descriptionText = "${device.displayName} ${name} was set to ${value}${unit}"
		if (txtEnable) log.info descriptionText
		sendEvent(name: name, value: value, descriptionText: descriptionText, unit: unit, stateChange: true)
	}

	state.lastRunningMode = newOS
	updateDataValue("lastRunningMode", newOS)
}




def on() {
    sendEvent(name: "thermostatMode", value: "on", descriptionText: "Thermostat Mode set to on", isStateChange: true)
	def ircode =  state.poweron   
     log.info "ircode = " + ircode
     EnviaComando(ircode)
	 log.info "Enviado o commando de thermostatMode =  on " 	


}

def off() {
    sendEvent(name: "thermostatMode", value: "off", descriptionText: "Thermostat Mode set to off", isStateChange: true)
	def ircode =  state.poweroff    
     EnviaComando(ircode)
	    log.info "Enviado o commando de thermostatMode =  off " 	

         
}




//Case para os botões se usar no Dashboard 
def push(pushed) {
	logDebug("push: button = ${pushed}")
	if (pushed == null) {
		logWarn("push: pushed is null.  Input ignored")
		return
	}
	pushed = pushed.toInteger()
	switch(pushed) {

		case 2 : auto(); break
		case 3 : heat(); break
		case 4 : cool(); break
        case 5 : fan(); break
        case 6 : dry(); break
        case 7 : setautocool(); break                
        case 8 : comandoextra1(); break    
        case 9 : comandoextra2(); break            
        case 10 : comandoextra3(); break            
        case 11 : comandoextra4(); break    
        case 12 : comandoextra5(); break    
        case 13 : fanAuto(); break    
        case 14 : fanLow(); break    
        case 15 : fanMed(); break    
        case 16 : fanHigh(); break   
        case 17 : comandoextra6(); break  
        case 18 : comandoextra7(); break  
        case 19 : comandoextra8(); break   
		case 20 : fastcold(); break
		case 21 : temp18(); break
		case 22 : temp20(); break
		case 23 : temp22(); break
		case 24 : clock(); break
		case 25 : sweep(); break
		case 26 : turbo(); break
		case 27 : fan(); break
		case 28 : temp17(); break
		case 29 : temp23(); break
		case 30 : temp26(); break
		case 31 : onoff(); break
		case 32 : temp19(); break
		case 33 : temp21(); break
		case 34 : swing(); break
		case 35 : manual(); break
		case 36 : mode(); break
		case 37 : up(); break
		case 38 : timer(); break
		case 39 : cancel(); break
		case 40 : down(); break
		case 41 : display(); break
		case 42 : io(); break
		case 43 : tempup(); break
		case 44 : tempdown(); break
		case 45 : fanspeed(); break
		case 46 : off(); break
		case 47 : poweron(); break
		
		
		default:
			logDebug("push: Botão inválido.")
			break
	}
}



//Botão #0 para dashboard
def poweroff(){
    sendEvent(name: "thermostatMode", value: "off", descriptionText: "Thermostat Mode set to off", isStateChange: true)
    def ircode =  state.poweroff
    EnviaComando(ircode)    

}

//Botão #1 para dashboard
def poweron(){
    sendEvent(name: "thermostatMode", value: "on", descriptionText: "Thermostat Mode set to on", isStateChange: true)
    def ircode =  state.poweron
    EnviaComando(ircode)    
}

//Botão #2 para dashboard
def auto(){
    sendEvent(name: "thermostatMode", value: "auto")
    def ircode =  state.auto
	if (ircode.length() < 30) {
		ircode = state.mode
	}	
    EnviaComando(ircode)    
    log.info "Enviado o commando de thermostatMode =  auto " 
	
}


//Botão #3 para dashboard
def heat(){
    sendEvent(name: "thermostatMode", value: "heat")
    def ircode =  state.heat
	if (ircode.length() < 30) {
		ircode = state.mode
		log.info "pego o mode para heat"
		log.info state.mode 		
	}	
    EnviaComando(ircode)    
    log.info "Enviado o commando de thermostatMode =  auto " 	
}

//Botão #4 para dashboard
def cool(){
    sendEvent(name: "thermostatMode", value: "cool")
    def ircode = state.cool
	if (ircode.length() < 30) {
		ircode = state.mode
		log.info "pego o mode para cool"
		log.info ircode
	}	
    EnviaComando(ircode)    
    log.info "Enviado o commando de thermostatMode =  cool " 	
}

//Botão #5 para dashboard
def fan(){
    sendEvent(name: "thermostatMode", value: "fan")
    def ircode =  state.fan
	if (ircode.length() < 30) {
		ircode = state.mode
	}	
    EnviaComando(ircode)    
    log.info "Enviado o commando de thermostatMode =  fan " 	
}


//Botão #6 para dashboard
def dry(){
    sendEvent(name: "thermostatMode", value: "dry")
    def ircode =   state.dry
	if (ircode.length() < 30) {
		ircode = state.mode
	}	
    EnviaComando(ircode)
}

//Botão #7 para dashboard
def setautocool(){
    sendEvent(name: "thermostatMode", value: "setautocool")
    def ircode =  state.setautocool
    EnviaComando(ircode)
}



//Botão #8 para dashboard
def comandoextra1(){
    sendEvent(name: "action", value: "comandoextra1")
    def ircode =   state.comandoextra1
    EnviaComando(ircode)
}

//Botão #9 para dashboard
def comandoextra2(){
    sendEvent(name: "action", value: "comandoextra2")
     def ircode =  state.comandoextra2
    EnviaComando(ircode)
}



//Botão #10 para dashboard
def comandoextra3(){
    sendEvent(name: "action", value: "comandoextra3")
    def ircode =  state.comandoextra3
    EnviaComando(ircode)
}

//Botão #11 para dashboard
def comandoextra4(){
    sendEvent(name: "action", value: "comandoextra4")
    def ircode =  state.comandoextra4
    EnviaComando(ircode)
}

//Botão #12 para dashboard
def comandoextra5(){
    sendEvent(name: "action", value: "comandoextra5")
    def ircode =  state.comandoextra5
    EnviaComando(ircode)
}


//Botão #13 para dashboard
def fanAuto(){
    sendEvent(name: "setThermostatFanMode", value: "fanAuto")
    sendEvent(name: "FanMode", value: "fanAuto")
    EnviaComando(ircode)    
    log.info "Enviado o commando de thermostatMode =  auto " 	
}



//Botão #14 para dashboard
def fanLow(){
    sendEvent(name: "setThermostatFanMode", value: "fanLow")
    def ircode =  state.fanLow
    EnviaComando(ircode)
    log.info "Enviado o commando de setThermostatFanMode =  fanLow " 	
	
}



//Botão #18 para dashboard
def fanMed(){
    sendEvent(name: "setThermostatFanMode", value: "fanMed")
   def ircode =   state.fanMed
    EnviaComando(ircode)    
    log.info "Enviado o commando de setThermostatFanMode =  fanMed " 	
	
}

//Botão #19 para dashboard
def fanHigh(){
    sendEvent(name: "setThermostatFanMode", value: "fanHigh")
    def ircode =  state.fanHigh
    EnviaComando(ircode)    
    log.info "Enviado o commando de setThermostatFanMode =  fanMed " 	
	
}

//Botão #21 para dashboard
def comandoextra6(){
    sendEvent(name: "action", value: "comandoextra6")
    def ircode = state.comandoextra6
    EnviaComando(ircode)    
}

//Botão #22 para dashboard
def comandoextra7(){
    sendEvent(name: "action", value: "comandoextra7")
    def ircode = state.comandoextra7
    EnviaComando(ircode)    
}


//Botão #23 para dashboard
def comandoextra8(){
    sendEvent(name: "action", value: "comandoextra8")
    def ircode =  state.comandoextra8
    EnviaComando(ircode)
}

//Botão #24 para dashboard
def fastcold(){
    sendEvent(name: "action", value: "fastcold")
   def ircode =  state.fastcold
    EnviaComando(ircode)
}

//Botão #25 para dashboard
def temp18(){
    sendEvent(name: "setCoolingSetpoint", value: 18 )
    def ircode =  state.temp18
    EnviaComando(ircode)
    log.info "Enviado o commando de setThermostatFanMode =  fanMed " 	
	
}


//Botão #26 para dashboard
def temp20(){
    sendEvent(name: "setCoolingSetpoint", value: 20 )
    def ircode =  state.temp20
    EnviaComando(ircode)
}

//Botão #27 para dashboard
def temp22(){
    sendEvent(name: "setCoolingSetpoint", value: 22 )
    def ircode =  state.temp22
    EnviaComando(ircode)
}

//Botão #28 para dashboard
def clock(){
    sendEvent(name: "action", value: "clock")
    def ircode =   state.clock
    EnviaComando(ircode)
}

//Botão #29 para dashboard
def sweep(){
    sendEvent(name: "action", value: "sweep")
    def ircode =  state.sweep
    EnviaComando(ircode)
}


//Botão #30 para dashboard
def turbo(){
    sendEvent(name: "action", value: "turbo")
    def ircode =  state.turbo
    EnviaComando(ircode)
}

//Botão #32 para dashboard
def temp17(){
    sendEvent(name: "setCoolingSetpoint", value: 17 )
    def ircode = state.temp17
    EnviaComando(ircode)
}

//Botão #33 para dashboard
def temp23(){
    sendEvent(name: "setCoolingSetpoint", value: 23 )
    def ircode =  state.temp23
    EnviaComando(ircode)
}

//Botão #34 para dashboard
def temp26(){
    sendEvent(name: "setCoolingSetpoint", value: 26 )
    def ircode =  state.temp26
    EnviaComando(ircode)
}

//Botão #35 para dashboard
def onoff(){
    sendEvent(name: "action", value: "onoff")
    def ircode =  state.onoff
    EnviaComando(ircode)
    log.info "Enviado o commando de setThermostatFanMode =  fanMed " 	
	
}

//Botão #36 para dashboard
def temp19(){
    sendEvent(name: "setCoolingSetpoint", value: 19 )
    def ircode =  state.temp19
    EnviaComando(ircode)
    log.info "Enviado o commando de setCoolingSetpoint =  19 " 	
	
}

//Botão #38 para dashboard
def temp21(){
    sendEvent(name: "setCoolingSetpoint", value: 21 )
    def ircode =   state.temp21
    EnviaComando(ircode)
    log.info "Enviado o commando de setCoolingSetpoint =  21 " 	
	
}

//Botão #39 para dashboard
def swing(){
    sendEvent(name: "action", value: "swing")
   def ircode =  state.swing
    EnviaComando(ircode)
}


//Botão #40 para dashboard
def manual(){
    sendEvent(name: "action", value: "manual")
    def ircode =  state.manual
    EnviaComando(ircode)
}

//Botão #41 para dashboard
def mode(){
    sendEvent(name: "action", value: "mode")
    def ircode =  state.mode
    EnviaComando(ircode)
    log.info "Enviado o commando de action =  mode " 	
	
}

//Botão #40 para dashboard
def up(){
    sendEvent(name: "action", value: "up")
    def ircode =  state.up
    EnviaComando(ircode)
    log.info "Enviado o commando de action =  up " 	
	
}


//Botão #40 para dashboard
def timer(){
    sendEvent(name: "action", value: "timer")
    def ircode =  state.timer
    EnviaComando(ircode)
}

//Botão #44 para dashboard
def cancel(){
    sendEvent(name: "action", value: "cancel")
    def ircode =  state.cancel
    EnviaComando(ircode)
}

//Botão #45 para dashboard
def down(){
    sendEvent(name: "action", value: "down")
    def ircode =  state.down
    EnviaComando(ircode)
    log.info "Enviado o commando de action =  down " 	
	
}

//Botão #46 para dashboard
def display(){
    sendEvent(name: "action", value: "display")
    def ircode =  state.display
    EnviaComando(ircode)
}

//Botão #47 para dashboard
def io(){
    sendEvent(name: "action", value: "io")
    def ircode =  state.io
    EnviaComando(ircode)
}

//Botão #48 para dashboard
def tempup(){
    sendEvent(name: "action", value: "tempup")
    def ircode =  state.tempup
    EnviaComando(ircode)
    log.info "Enviado o commando de action =  tempup " 	
	
}

//Botão #49 para dashboard
def tempdown(){
    sendEvent(name: "action", value: "tempdown")
    def ircode =  state.tempdown
    EnviaComando(ircode)
    log.info "Enviado o commando de action =  tempdown " 	
	
}

//Botão #50 para dashboard
def fanspeed(){
    sendEvent(name: "action", value: "fanspeed")
    def ircode =  state.fanspeed
    EnviaComando(ircode)
}


def setCoolingSetpoint(temperature) {    
    if (device.currentValue("thermostatMode") == "cool") {               
        logInfo "Colocando a temp em  ${temperature}"  
        def previousTempCool = device.currentValue("coolingSetpoint")
        
        if (previousTempCool <= temperature) {
            tempup()
            sendEvent(name: "setCoolingSetpoint", value: temperature )
            logInfo "Aumentando a temp"
        } else {
            sendEvent(name: "setCoolingSetpoint", value: temperature )
            tempdown()
            logInfo "Diminuindo a temp"
        }
    } else {
        log.warn "Sem alterar a temp para  ${temperature}, porque o modo atual é ${device.currentValue("thermostatMode")}"
    }
}

def setHeatingSetpoint(temperature) {    
    if (device.currentValue("thermostatMode") == "heat") {               
        logInfo "Colocando a temp em  ${temperature}"  
        def previousTempHeat = device.currentValue("heatingSetpoint")
        
        if (previousTempHeat <= temperature) {
            tempup()
            sendEvent(name: "setHeatingSetpoint", value: temperature )
            logInfo "Aumentando a temp"
        } else {
            sendEvent(name: "setHeatingSetpoint", value: temperature )
            tempdown()
            logInfo "Diminuindo a temp"
        }
    } else {
        log.warn "Sem alterar a temp para  ${temperature}, porque o modo atual é ${device.currentValue("thermostatMode")}"
    }
}


def setThermostatFanMode(fanmode) {
    switch(fanmode) {
        case "on":
            fan()
            break
        case "circulate":
            //
            break
        case "auto":
            auto()
            break
        case "quiet":
            //
            break
        case "low":
            fanLow()
            break
        case "mid":
            fanMed()
            break
        case "high":
            fanHigh()
            break        
        case "4":
            //
            break
        default:
            log.warn "Unknown fan mode ${fanmode}"
            break
    }
}


def convertCelciusToLocalTemp(temp) {
    return (location.temperatureScale == "F") ? ((temp * 1.8) + 32) : temp
}

def convertLocalToCelsiusTemp(temp) {
    return (location.temperatureScale == "F") ? Math.round((temp - 32) / 1.8) : temp
}



def setThermostatMode(thermostatmode) {
    switch(thermostatmode) {
        case "auto":
            auto()
            break
        case "off":
            off()
            break
        case "heat":
            heat()
            break
        case "emergency heat":
            emergencyHeat()
            break
        case "cool":
            cool()
            break
        case "fan":
            fan()
            break
        case "dry":
            dry()
            break
        default:
            log.warn "Unknown mode ${thermostatmode}"
            break
    }
}




def info(msg) {
    if (logLevel == "INFO" || logLevel == "DEBUG") {
        log.info(msg)
    }
}

def logInfo(msg) {
    if (logEnable) log.info msg
}



//DEBUG
private logDebug(msg) {
  if (settings?.debugOutput || settings?.debugOutput == null) {
    log.debug "$msg"
  }
}



void logInfo(String msg) {
    if ((Boolean)settings.logInfo != false) {
        log.info "${drvThis}: ${msg}"
    }
}

void logTrace(String msg) {
    if ((Boolean)settings.logTrace != false) {
        log.trace "${drvThis}: ${msg}"
    }
}

void logWarn(String msg, boolean force = false) {
    if (force || (Boolean)settings.logWarn != false) {
        log.warn "${drvThis}: ${msg}"
    }
}

void logError(String msg) {
    log.error "${drvThis}: ${msg}"
}




private getDescriptionText(msg) {
	def descriptionText = "${device.displayName} ${msg}"
	if (settings?.txtEnable) log.info "${descriptionText}"
	return descriptionText
}


def logsOff() {
    log.warn 'logging disabled...'
    device.updateSetting('logInfo', [value:'false', type:'bool'])
    device.updateSetting('logWarn', [value:'false', type:'bool'])
    device.updateSetting('logDebug', [value:'false', type:'bool'])
    device.updateSetting('logTrace', [value:'false', type:'bool'])
}
