/**
 *   ControlArt 7Port Driver - Versão Usando IR MolSmart Database. Versão para controles de TV e Som. 
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
 *              V.1.0   6/5/2025 - V1 para trazer os controles remotos prontos. 

 *
 */
metadata {
  definition (name: "ControlArt - 7port - TV (irweb)", namespace: "TRATO", author: "VH", vid: "generic-contact") {
    capability "TV"  
    capability "SamsungTV"
   	capability "Switch"  
   	capability "Actuator"
   	capability "PushableButton"
   	capability "Variable"      
	capability "Initialize"       
  
	attribute "channel", "number"
	attribute "volume", "number"
	attribute "movieMode", "string"
	attribute "power", "string"
	attribute "sound", "string"
	attribute "picture", "string"  
    attribute "Controle", "string"  
    attribute "TipoControle", "string" 
    attribute "Formato", "string"       
    attribute "lastCommandStatus", "string" // To track command delivery status
      
    command "GetRemoteDATA"
    command "cleanvars"
    command "AtualizaDados7Port"
	command "reconnect"
	command "refresh"
    command "checkConnection" // Added explicit connection check command    
      
command "poweroff"
command "poweron"
command "mute"
command "source"
command "back"
command "menu"
command "hdmi"
command "hdmi"
command "left"
command "right"
command "up"
command "down"
command "confirm"
command "exit"
command "home"
command "channelUp"
command "channelDown"
command "volumeUp"
command "volumeDown"
command "num0"
command "num1"
command "num2"
command "num3"
command "num4"
command "num5"
command "num6"
command "num7"
command "num8"
command "num9" 
command "btnextra"
command "btnextra"
command "btnextra"
command "appAmazonPrime"
command "appYouTube"
command "appNetflix"
command "btnextra"
command "btnextra5"
command "btnextra6"
command "btnextra7"
command "btnAIRsend"
command "btnBIRsend"
command "btnCIRsend"
command "btnDIRsend"
command "playIRsend"
command "pauseIRsend"
command "nextIRsend"
command "guideIRsend"
command "infoIRsend" 
command "toolsIRsend" 
command "smarthubIRsend" 
command "previouschannelIRsend" 
command "backIRsend"	  
		  
  }
      
      


}

    import groovy.transform.Field
    @Field static final String DRIVER = "by TRATO"
    @Field static final String USER_GUIDE = "https://github.com/hhorigian/hubitat_MolSmart_GW3_IR/tree/main/TV"

    String fmtHelpInfo(String str) {
    String prefLink = "<a href='${USER_GUIDE}' target='_blank'>${str}<br><div style='font-size: 70%;'>${DRIVER}</div></a>"
    return "<div style='font-size: 160%; font-style: bold; padding: 2px 0px; text-align: center;'>${prefLink}</div>"
    }


    @Field static final String DRIVER1 = "IR MolSmart"
    @Field static final String USER_GUIDE1 = "https://ir.molsmart.com.br/"

// Connection parameters
@Field static final Integer RECONNECT_DELAY = 30 // seconds between reconnect attempts
@Field static final Integer CONNECTION_TIMEOUT = 10 // seconds for connection timeout
@Field static final Integer KEEPALIVE_INTERVAL = 300 // seconds between keepalive checks
@Field static final Integer MAX_RETRY_ATTEMPTS = 3 // max retry attempts for sending commands


    String fmtHelpInfo1(String str) {
    String prefLink1 = "<a href='${USER_GUIDE1}' target='_blank'>${str}<br><div style='font-size: 70%;'>${DRIVER1}</div></a>"
    return "<div style='font-size: 160%; font-style: bold; padding: 2px 0px; text-align: center;'>${prefLink1}</div>"
    }



  preferences {
	    input name: "logEnable", type: "bool", title: "Enable debug logging", defaultValue: false
        input name: "molIPAddress", type: "text", title: "ControlArt IP Address", submitOnChange: true, required: true, defaultValue: "192.168.1.100" 
	    input name: "channel", title:"Canal Infravermelho (1-8)", type: "string", required: true        
	    input "device_port", "number", title: "Port of 7port", required: true, defaultValue: 4998
        input name: "webserviceurl", title:"URL Do Controle Remoto", type: "string"

        //help guide
        input name: "UserGuide", type: "hidden", title: fmtHelpInfo("Manual do Driver") 
        input name: "SiteIR", type: "hidden", title: fmtHelpInfo1("Site IR MolSmart") 

  }   
  

def installed()
{   
    log.debug "installed()"
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


def initialize()
{
    sendEvent(name:"numberOfButtons", value:60)  		
    log.debug "initialized()"

    // Initialize connection state
    state.connectionAttempts = 0
    state.lastCommand = ""
    state.lastCommandAttempts = 0
    
    // Start connection process
    connectToDevice()    
    
}

def cleanvars()  //Usada para limpar todos os states e controles aprendidos. 
{
//state.remove()
  state.clear() 
  AtualizaDadosGW3()  
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


private EnviaComando(command,acao) {
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
                
    state.encoding = resp.data.conversor
    state.OFFIRsend  = resp.data.functions.function[0]
    state.OnIRsend  = resp.data.functions.function[1]
    state.muteIRsend  = resp.data.functions.function[2]             
    state.sourceIRsend  = resp.data.functions.function[3]     
    state.backIRsend  = resp.data.functions.function[4]     
    state.menuIRsend  = resp.data.functions.function[5]     
    state.hdmi1IRsend  = resp.data.functions.function[6]     
    state.hdmi2IRsend  = resp.data.functions.function[7]     
    state.leftIRsend  = resp.data.functions.function[8]     
    state.rightIRsend  = resp.data.functions.function[9]     
    state.upIRsend  = resp.data.functions.function[10]     
    state.downIRsend  = resp.data.functions.function[11]     
    state.confirmIRsend  = resp.data.functions.function[12]     
    state.exitIRsend  = resp.data.functions.function[13]     
    state.homeIRsend  = resp.data.functions.function[14]                 
    state.ChanUpIRsend  = resp.data.functions.function[15]
    state.ChanDownIRsend  = resp.data.functions.function[16]     
    state.VolUpIRsend  = resp.data.functions.function[17]     
    state.VolDownIRsend  = resp.data.functions.function[18]     
    state.num0IRsend  = resp.data.functions.function[19]     
    state.num1IRsend  = resp.data.functions.function[20]     
    state.num2IRsend  = resp.data.functions.function[21]     
    state.num3IRsend  = resp.data.functions.function[22]     
    state.num4IRsend  = resp.data.functions.function[23]     
    state.num5IRsend  = resp.data.functions.function[24]     
    state.num6IRsend  = resp.data.functions.function[25]     
    state.num7IRsend  = resp.data.functions.function[26]     
    state.num8IRsend  = resp.data.functions.function[27]     
    state.num9IRsend  = resp.data.functions.function[28]     
    state.btnextra1IRsend  = resp.data.functions.function[29]     
    state.btnextra2IRsend  = resp.data.functions.function[30]     
    state.btnextra3IRsend  = resp.data.functions.function[31]     
    state.amazonIRsend  = resp.data.functions.function[32]     
    state.youtubeIRsend  = resp.data.functions.function[33]     
    state.netflixIRsend  = resp.data.functions.function[34]     
    state.btnextra4IRsend  = resp.data.functions.function[35]     
    state.btnextra5IRsend  = resp.data.functions.function[36]     
    state.btnextra6IRsend  = resp.data.functions.function[37]     
    state.btnextra7IRsend  = resp.data.functions.function[38]   
    state.btnAIRsend  = resp.data.functions.function[39] 
    state.btnBIRsend  = resp.data.functions.function[40] 
    state.btnCIRsend  = resp.data.functions.function[41] 
    state.btnDIRsend  = resp.data.functions.function[42] 
    state.playIRsend  = resp.data.functions.function[43] 
    state.pauseIRsend  = resp.data.functions.function[44] 
    state.nextIRsend  = resp.data.functions.function[45] 
    state.guideIRsend  = resp.data.functions.function[46]                    
    state.infoIRsend   = resp.data.functions.function[47] 
    state.toolsIRsend  = resp.data.functions.function[48] 
    state.smarthubIRsend  = resp.data.functions.function[49] 
    state.previouschannelIRsend  = resp.data.functions.function[50] 
    state.backIRsend  = resp.data.functions.function[51]
             
    }
            
	}
    } catch (Exception e) {
        log.warn "Get Remote Control Info failed: ${e.message}"
    }    

}




//Basico on / off para Switch 
def on() {
     sendEvent(name: "switch", value: "on", isStateChange: true)
     def ircode =  state.OnIRsend   
     log.info "ircode = " + ircode
     EnviaComando(ircode,"poweron")

}

def off() {
     sendEvent(name: "switch", value: "off", isStateChange: true)
     def ircode =  state.OFFIRsend    
     EnviaComando(ircode,"poweroff")
         
}


//Case para los botones de push en el dashboard. 
def push(pushed) {
	logDebug("push: button = ${pushed}")
	if (pushed == null) {
		logWarn("push: pushed is null.  Input ignored")
		return
	}
	pushed = pushed.toInteger()
    //pushed = pushed.toString()
	switch(pushed) {
        case 0 : poweroff(); break
	case 1 : poweron(); break
        case 2 : mute(); break
	case 3 : source(); break
	case 4 : back(); break
        case 5 : menu(); break
        case 6 : hdmi1(); break
        case 7 : hdmi2(); break                
	case 8 : left(); break
	case 9 : right(); break
	case 10: up(); break
	case 11: down(); break
	case 12: confirm(); break
	case 13: exit(); break
	case 14: home(); break
	case 18: channelUp(); break
	case 19: channelDown(); break
	case 21: volumeUp(); break
	case 22: volumeDown(); break
	case 23: num0(); break
	case 24: num1(); break
	case 25: num2(); break
	case 26: num3(); break
	case 27: num4(); break        
	case 28: num5(); break
	case 29: num6(); break
	case 30: num7(); break
	case 31: num8(); break            
	case 32: num9(); break   
	case 33: btnextra1(); break                
	case 34: btnextra2(); break
	case 35: btnextra3(); break
	case 38: appAmazonPrime(); break
	case 39: appYouTube(); break
	case 40: appNetflix(); break    
	case 41: btnextra4(); break    
        case 42: btnextra5(); break    
        case 43: btnextra6(); break    
        case 44: btnextra7(); break    
        case 45: btnAIRsend(); break    
        case 46: btnBIRsend(); break    
        case 47: btnCIRsend(); break    
        case 48: btnDIRsend(); break    
        case 49: playIRsend(); break    
        case 50: pauseIRsend(); break    
        case 51: nextIRsend(); break    
        case 52: guideIRsend(); break            
        case 53: infoIRsend(); break 
        case 54: toolsIRsend(); break 
        case 55: smarthubIRsend(); break 
        case 56: previouschannelIRsend(); break 
        case 57: backIRsend(); break        
         
        default:
            //pushed = pushed.toString()
		logDebug("push: Botão inválido.")
		break
	}
}

//Botão #0 para dashboard
def poweroff(){
	sendEvent(name: "power", value: "off")
    def ircode =  state.OFFIRsend
    EnviaComando(ircode,"poweroff")    
}

//Botão #1 para dashboard
def poweron(){
	sendEvent(name: "power", value: "on")
    def ircode =  state.OnIRsend
    EnviaComando(ircode,"poweron")    
}

//Botão #2 para dashboard
def mute(){
	sendEvent(name: "action", value: "mute")
    def ircode =  state.muteIRsend
    EnviaComando(ircode,"mute")    
}


//Botão #3 para dashboard
def source(){
	sendEvent(name: "action", value: "source")
    def ircode =  state.sourceIRsend
    EnviaComando(ircode,"source")    
}

//Botão #4 para dashboard
def back(){
	sendEvent(name: "action", value: "back")
    def ircode = state.backIRsend
    EnviaComando(ircode,"back")    
}

//Botão #5 para dashboard
def menu(){
	sendEvent(name: "action", value: "menu")
    def ircode =  state.menuIRsend
    EnviaComando(ircode,"menu")    
}


//Botão #6 para dashboard
def hdmi1(){
    sendEvent(name: "input", value: "hdmi1")
    def ircode =   state.hdmi1IRsend
    EnviaComando(ircode,"hdmi1")
}

//Botão #7 para dashboard
def hdmi2(){
    sendEvent(name: "input", value: "hdmi2")
    def ircode =  state.hdmi2IRsend
    EnviaComando(ircode,"hdmi2")
}



//Botão #8 para dashboard
def left(){
    sendEvent(name: "action", value: "left")
    def ircode =   state.leftIRsend
    EnviaComando(ircode,"left")
}

//Botão #9 para dashboard
def right(){
    sendEvent(name: "action", value: "right")
     def ircode =  state.rightIRsend
    EnviaComando(ircode,"right")
}



//Botão #10 para dashboard
def up(){
    sendEvent(name: "action", value: "up")
    def ircode =  state.upIRsend
    EnviaComando(ircode,"up")
}

//Botão #11 para dashboard
def down(){
    sendEvent(name: "action", value: "down")
    def ircode =  state.downIRsend
    EnviaComando(ircode,"down")
}

//Botão #12 para dashboard
def confirm(){
    sendEvent(name: "action", value: "confirm")
    def ircode =  state.confirmIRsend
    EnviaComando(ircode,"confirm")
}


//Botão #13 para dashboard
def exit(){
	sendEvent(name: "action", value: "exit")
    def ircode =  state.exitIRsend
    EnviaComando(ircode,"exit")    
}



//Botão #14 para dashboard
def home(){
    sendEvent(name: "action", value: "home")
    def ircode =  state.homeIRsend
    EnviaComando(ircode,"home")
}



//Botão #18 para dashboard
def channelUp(){
	sendEvent(name: "channel", value: "chup")
   def ircode =   state.ChanUpIRsend
    EnviaComando(ircode,"channelUp")    
}

//Botão #19 para dashboard
def channelDown(){
	sendEvent(name: "channel", value: "chdown")
    def ircode =  state.ChanDownIRsend
    EnviaComando(ircode,"channelDown")    
}

//Botão #21 para dashboard
def volumeUp(){
	sendEvent(name: "action", value: "volup")
    def ircode = state.VolUpIRsend
    EnviaComando(ircode,"volumeUp")    
}

//Botão #22 para dashboard
def volumeDown(){
	sendEvent(name: "action", value: "voldown")
    def ircode = state.VolDownIRsend
    EnviaComando(ircode,"volumeDown")    
}


//Botão #23 para dashboard
def num0(){
    sendEvent(name: "action", value: "num0")
    def ircode =  state.num0IRsend
    EnviaComando(ircode,"num0")
}

//Botão #24 para dashboard
def num1(){
    sendEvent(name: "action", value: "num1")
   def ircode =  state.num1IRsend
    EnviaComando(ircode,"num1")
}

//Botão #25 para dashboard
def num2(){
    sendEvent(name: "action", value: "num2")
    def ircode =  state.num2IRsend
    EnviaComando(ircode,"num2")
}


//Botão #26 para dashboard
def num3(){
    sendEvent(name: "action", value: "num3")
    def ircode =  state.num3IRsend
    EnviaComando(ircode,"num3")
}

//Botão #27 para dashboard
def num4(){
    sendEvent(name: "action", value: "num4")
    def ircode =  state.num4IRsend
    EnviaComando(ircode,"num4")
}

//Botão #28 para dashboard
def num5(){
    sendEvent(name: "action", value: "num5")
    def ircode =   state.num5IRsend
    EnviaComando(ircode,"num5")
}

//Botão #29 para dashboard
def num6(){
    sendEvent(name: "action", value: "num6")
    def ircode =  state.num6IRsend
    EnviaComando(ircode,"num6")
}


//Botão #30 para dashboard
def num7(){
    sendEvent(name: "action", value: "num7")
    def ircode =  state.num7IRsend
    EnviaComando(ircode,"num7")
}

//Botão #31 para dashboard
def num8(){
    sendEvent(name: "action", value: "num8")
    def ircode =  state.num8IRsend
    EnviaComando(ircode,"num8")
}

//Botão #32 para dashboard
def num9(){
    sendEvent(name: "action", value: "num9")
    def ircode = state.num9IRsend
    EnviaComando(ircode,"num9")
}

//Botão #33 para dashboard
def btnextra1(){
    sendEvent(name: "action", value: "confirm")
    def ircode =  state.btnextra1IRsend
    EnviaComando(ircode,"btnextra1")
}

//Botão #34 para dashboard
def btnextra2(){
    sendEvent(name: "action", value: "btnextra2")
    def ircode =  state.btnextra2IRsend
    EnviaComando(ircode,"btnextra2")
}

//Botão #35 para dashboard
def btnextra3(){
    sendEvent(name: "action", value: "btnextra3")
    def ircode =  state.btnextra3IRsend
    EnviaComando(ircode,"btnextra3")
}

//Botão #38 para dashboard
def appAmazonPrime(){
    sendEvent(name: "input", value: "amazon")
    def ircode =   state.amazonIRsend
    EnviaComando(ircode,"appAmazonPrime")
}

//Botão #39 para dashboard
def appyoutube(){
    sendEvent(name: "input", value: "youtube")
   def ircode =  state.youtubeIRsend
    EnviaComando(ircode,"appyoutube")
}


//Botão #40 para dashboard
def appnetflix(){
    sendEvent(name: "input", value: "netflix")
    def ircode =  state.netflixIRsend
    EnviaComando(ircode,"appnetflix")
}

//Botão #41 para dashboard
def btnextra4(){
    sendEvent(name: "action", value: "btnextra4")
    def ircode =  state.btnextra4IRsend
    EnviaComando(ircode,"btnextra4")
}

//Botão #40 para dashboard
def btnextra5(){
    sendEvent(name: "action", value: "btnextra5")
    def ircode =  state.btnextra5IRsend
    EnviaComando(ircode,"btnextra5")
}


//Botão #40 para dashboard
def btnextra6(){
    sendEvent(name: "action", value: "btnextra6")
    def ircode =  state.btnextra6IRsend
    EnviaComando(ircode,"btnextra6")
}

//Botão #44 para dashboard
def btnextra7(){
    sendEvent(name: "action", value: "btnextra7")
    def ircode =  state.btnextra7IRsend
    EnviaComando(ircode,"btnextra7")
}

//Botão #45 para dashboard
def btnAIRsend(){
    sendEvent(name: "action", value: "btnAIRsend")
    def ircode =  state.btnAIRsend
    EnviaComando(ircode,"btnAIRsend")
}

//Botão #46 para dashboard
def btnBIRsend(){
    sendEvent(name: "action", value: "btnBIRsend")
    def ircode =  state.btnBIRsend
    EnviaComando(ircode,"btnBIRsend")
}

//Botão #47 para dashboard
def btnCIRsend(){
    sendEvent(name: "action", value: "btnCIRsend")
    def ircode =  state.btnCIRsend
    EnviaComando(ircode,"btnCIRsend")
}

//Botão #48 para dashboard
def btnDIRsend(){
    sendEvent(name: "action", value: "btnDIRsend")
    def ircode =  state.btnDIRsend
    EnviaComando(ircode,"btnDIRsend")
}

//Botão #49 para dashboard
def playIRsend(){
    sendEvent(name: "action", value: "playIRsend")
    def ircode =  state.playIRsend
    EnviaComando(ircode,"playIRsend")
}

//Botão #50 para dashboard
def pauseIRsend(){
    sendEvent(name: "action", value: "pauseIRsend")
    def ircode =  state.pauseIRsend
    EnviaComando(ircode,"pauseIRsend")
}

//Botão #51 para dashboard
def nextIRsend(){
    sendEvent(name: "action", value: "nextIRsend")
    def ircode =  state.nextIRsend
    EnviaComando(ircode,"nextIRsend")
}

//Botão #52 para dashboard
def guideIRsend(){
    sendEvent(name: "action", value: "guideIRsend")
    def ircode =  state.guideIRsend
    EnviaComando(ircode,"guideIRsend")
}

//Botão #53 para dashboard
def infoIRsend(){
    sendEvent(name: "action", value: "infoIRsend")
    def ircode =  state.infoIRsend
    EnviaComando(ircode,"infoIRsend")
}

//Botão #54 para dashboard
def toolsIRsend(){
    sendEvent(name: "action", value: "toolsIRsend")
    def ircode =  state.toolsIRsend
    EnviaComando(ircode,"toolsIRsend")
}

//Botão #55 para dashboard
def smarthubIRsend(){
    sendEvent(name: "action", value: "smarthubIRsend")
    def ircode =  state.smarthubIRsend
    EnviaComando(ircode,"smarthubIRsend")
}

//Botão #56 para dashboard
def previouschannelIRsend(){
    sendEvent(name: "action", value: "previouschannelIRsend")
    def ircode =  state.previouschannelIRsend
    EnviaComando(ircode,"previouschannelIRsend")
}

//Botão #57 para dashboard
def backIRsend(){
    sendEvent(name: "action", value: "backIRsend")
    def ircode =  state.backIRsend
    EnviaComando(ircode,"backIRsend")
}

      

def info(msg) {
    if (logLevel == "INFO" || logLevel == "DEBUG") {
        log.info(msg)
    }
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

