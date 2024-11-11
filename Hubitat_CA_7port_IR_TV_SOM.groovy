/**
 *  ControlArt 7port Driver - IR para TV. Usando 7port
 *
 *  Copyright 2024 VH 
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
 *            --- Driver para XPort - IR - para TV  e Som --- Usando Controles criados na 7Port 
 *           v.1.0  5/11/2024  - BETA. 
 *           v.1.1  11/11/2024 - Corregidos os comandos de envio. 
 *
 */
metadata {
  definition (name: "ControlArt - 7port - IR para TV e SOM", namespace: "VH", author: "VH", vid: "generic-contact") {
    capability "Switch"  
    capability "Actuator"
    capability "TV"  
    capability "SamsungTV"
    capability "PushableButton"
	capability "Variable"  
    capability "Configuration"
    capability "Initialize"       
  
    attribute "boardstatus", "string"   
	attribute "channel", "number"
	attribute "volume", "number"
	attribute "movieMode", "string"
	attribute "power", "string"
	attribute "sound", "string"
	attribute "picture", "string"  
   
      command "CodigoHEX_mute", [
            [name: "HEXcode", type: "STRING", description: "MUTE = COLAR O CODIGO SendIR"]
        ]          
        command "CodigoHEX_source", [
            [name: "HEXcode", type: "STRING", description: "SOURCE = COLAR O CODIGO SendIR"]
        ]      
        command "CodigoHEX_back", [
            [name: "HEXcode", type: "STRING", description: "BACK = COLAR O CODIGO SendIR"]
        ]         
        command "CodigoHEX_menu", [
            [name: "HEXcode", type: "STRING", description: "MENU = COLAR O CODIGO SendIR"]
        ]      
        command "CodigoHEX_hdmi1", [
            [name: "HEXcode", type: "STRING", description: "HDMI1 = COLAR O CODIGO SendIR"]
        ]      
   	    command "CodigoHEX_hdmi2" , [
            [name: "HEXcode", type: "STRING", description: "HDMI2 = COLAR O CODIGO SendIR"]
        ]       
        command "CodigoHEX_up", [
            [name: "HEXcode", type: "STRING", description: "UP = COLAR O CODIGO SendIR"]
        ]      
    	command "CodigoHEX_down", [
            [name: "HEXcode", type: "STRING", description: "DOWN = COLAR O CODIGO SendIR"]
        ]      
    	command "CodigoHEX_right", [
            [name: "HEXcode", type: "STRING", description: "RIGHT = COLAR O CODIGO SendIR"]
        ]      
    	command "CodigoHEX_left", [
            [name: "HEXcode", type: "STRING", description: "LEFT = COLAR O CODIGO SendIR"]
        ]      
   	    command "CodigoHEX_confirm", [
            [name: "HEXcode", type: "STRING", description: "CONFIRM = COLAR O CODIGO SendIR"]
        ]      
        command "CodigoHEX_exit"   , [
            [name: "HEXcode", type: "STRING", description: "EXIT = COLAR O CODIGO SendIR"]
        ]      
        command "CodigoHEX_home", [
            [name: "HEXcode", type: "STRING", description: "HOME = COLAR O CODIGO SendIR"]
        ]      
        command "CodigoHEX_channelUp", [
            [name: "HEXcode", type: "STRING", description: "CHANUP = COLAR O CODIGO SendIR"]
        ]      
        command "CodigoHEX_channelDown", [
            [name: "HEXcode", type: "STRING", description: "CHANDOWN = COLAR O CODIGO SendIR"]
        ]      
        command "CodigoHEX_volumeUp", [
            [name: "HEXcode", type: "STRING", description: "VOLUP = COLAR O CODIGO SendIR"]
        ]      
   	    command "CodigoHEX_volumeDown", [
            [name: "HEXcode", type: "STRING", description: "VOLDOWN = COLAR O CODIGO SendIR"]
        ]      
    	command "CodigoHEX_num0" , [
            [name: "HEXcode", type: "STRING", description: "NUM0 = COLAR O CODIGO SendIR"]
        ]      
        command "CodigoHEX_num1", [
            [name: "HEXcode", type: "STRING", description: "NUM1 = COLAR O CODIGO SendIR"]
        ]      
    	command "CodigoHEX_num2", [
            [name: "HEXcode", type: "STRING", description: "NUM2 = COLAR O CODIGO SendIR"]
        ]      
    	command "CodigoHEX_num3", [
            [name: "HEXcode", type: "STRING", description: "NUM3 = COLAR O CODIGO SendIR"]
        ]      
    	command "CodigoHEX_num4", [
            [name: "HEXcode", type: "STRING", description: "NUM4 = COLAR O CODIGO SendIR"]
        ]      
    	command "CodigoHEX_num5", [
            [name: "HEXcode", type: "STRING", description: "NUM5 = COLAR O CODIGO SendIR"]
        ]      
    	command "CodigoHEX_num6", [
            [name: "HEXcode", type: "STRING", description: "NUM6 = COLAR O CODIGO SendIR"]
        ]      
    	command "CodigoHEX_num7", [
            [name: "HEXcode", type: "STRING", description: "NUM7 = COLAR O CODIGO SendIR"]
        ]      
    	command "CodigoHEX_num8", [
            [name: "HEXcode", type: "STRING", description: "NUM8 = COLAR O CODIGO SendIR"]
        ]      
    	command "CodigoHEX_num9", [
            [name: "HEXcode", type: "STRING", description: "NUM9 = COLAR O CODIGO SendIR"]
        ]      
        command "CodigoHEX_btnextra1" , [
            [name: "HEXcode", type: "STRING", description: "EXTRA1 = COLAR O CODIGO SendIR"]
        ]      
    	command "CodigoHEX_btnextra2"  , [
            [name: "HEXcode", type: "STRING", description: "EXTRA2 = COLAR O CODIGO SendIR"]
        ]           
    	command "CodigoHEX_btnextra3"  , [
            [name: "HEXcode", type: "STRING", description: "EXTRA3 = COLAR O CODIGO SendIR"]
        ]        
        command "CodigoHEX_APP_NETFLIX" , [
            [name: "HEXcode", type: "STRING", description: "NETFLIX = COLAR O CODIGO SendIR"]
        ]      
        command "CodigoHEX_APP_AMAZON", [
            [name: "HEXcode", type: "STRING", description: "AMAZON = COLAR O CODIGO SendIR"]
        ]      
        command "CodigoHEX_APP_YOUTUBE"  , [
            [name: "HEXcode", type: "STRING", description: "YOUTUBE = COLAR O CODIGO SendIR"]
        ]  
        command "CodigoHEX_ON"  , [
            [name: "HEXcode", type: "STRING", description: "ON = COLAR O CODIGO SendIR"]
        ]  
        command "CodigoHEX_OFF"  , [
            [name: "HEXcode", type: "STRING", description: "OFF = COLAR O CODIGO SendIR"]
        ]          

      
          
  }
      
}

import groovy.json.JsonSlurper
import groovy.transform.Field

command "keepalive"
command "reconnect"
command "refresh"

    import groovy.transform.Field
    @Field static final String DRIVER = "by TRATO"
    @Field static final String USER_GUIDE = "https://github.com/hhorigian/Controlart_7port"


    String fmtHelpInfo(String str) {
    String prefLink = "<a href='${USER_GUIDE}' target='_blank'>${str}<br><div style='font-size: 70%;'>${DRIVER}</div></a>"
    return "<div style='font-size: 160%; font-style: bold; padding: 2px 0px; text-align: center;'>${prefLink}</div>"
    }


  preferences {
    input "device_IP_address", "text", title: "IP Address of 7port", required: true
    input "device_port", "number", title: "Port of 7port", required: true, defaultValue: 4998
    input name: "logEnable", type: "bool", title: "Enable debug logging", defaultValue: false
    input 'logInfo', 'bool', title: 'Show Info Logs?',  required: false, defaultValue: true
    input 'logWarn', 'bool', title: 'Show Warning Logs?', required: false, defaultValue: true
    input 'logDebug', 'bool', title: 'Show Debug Logs?', description: 'Only leave on when required', required: false, defaultValue: true
    input 'logTrace', 'bool', title: 'Show Detailed Logs?', description: 'Only leave on when required', required: false, defaultValue: true

    //help guide
    input name: "UserGuide", type: "hidden", title: fmtHelpInfo("Manual do Driver") 

      
  }   

@Field static String partialMessage = ''
@Field static Integer checkInterval = 150

def installed() {
    logTrace('installed()')
    boardstatusvar = "offline"
    runIn(1800, logsOff)
} //OK
    
def uninstalled() {
    logTrace('uninstalled()')
    unschedule()
    interfaces.rawSocket.close()
}

def updated() {
    logTrace('updated()')
}



def reconnect () {
    interfaces.rawSocket.close();
    state.lastMessageReceived = ""
    state.lastmessage = ""


    try {
        logTrace("tentando conexão com o device no ${device_IP_address}...na porta ${device_port}");
        interfaces.rawSocket.connect(device_IP_address, (int) device_port);
        state.lastMessageReceivedAt = now();
        runIn(checkInterval, "connectionCheck");
    }
    catch (e) {
         logError( "${device_IP_address} keepalive error: ${e.message}" )
         sendEvent(name: "boardstatus", value: "offline", isStateChange: true)        
    }
    pauseExecution(500)    
    getmac()
    
}
 

def keepalive() {
    logTrace('keepalive()')
    unschedule()
    state.lastMessageReceived = ""
    state.lastmessage = ""
    interfaces.rawSocket.close();
    
    try {
        logTrace("tentando conexão com o device no ${device_IP_address}...na porta ${device_port}");
        interfaces.rawSocket.connect(device_IP_address, (int) device_port);
        state.lastMessageReceivedAt = now();
        runIn(checkInterval, "connectionCheck");
    }
    catch (e) {
         logError( "${device_IP_address} keepalive error: ${e.message}" )
         sendEvent(name: "boardstatus", value: "offline", isStateChange: true)        
    }
    pauseExecution(500)    
    
    
}


def initialize() {
    unschedule()
    logTrace('Run Initialize()')
    interfaces.rawSocket.close();    
    boardstatusvar = "offline"    
    
    if (!device_IP_address) {
        logError 'IP do Device not configured'
        return
    }

    if (!device_port) {
        logError 'Porta do Device não configurada.'
        return
    }
    
    
    try {
        logTrace("Initialize: Tentando conexão com o device no ${device_IP_address}...na porta configurada: ${device_port}");
        interfaces.rawSocket.connect(device_IP_address, (int) device_port);
        state.lastMessageReceivedAt = now();        
        if (boardstatusvar == "offline") { 
            sendEvent(name: "boardstatus", value: "online", isStateChange: true)    
            boardstatusvar = "online"
        }
        boardstatusvar = "online"
        runIn(checkInterval, "connectionCheck");
        
    }
    catch (e) {
        logError( "Initialize: com ${device_IP_address} com um error: ${e.message}" )
        boardstatusvar = "offline"
        runIn(60, "initialize");
    }    
       runIn(10, "refresh");
    
}



def refresh() {
    //def msg = "mdcmd_getmd," + state.newmacdec
    def msg = "sendir,1:8,1,38000,1,1,170,168,21,62,21,20,21,22,44,33"
    logTrace('Sent refresh()')   
    EnviaComando(msg)
}


////////////////////////////
//// Connections Checks ////
////////////////////////////

def connectionCheck() {
    def now = now();
    
    if ( now - state.lastMessageReceivedAt > (checkInterval * 1000)) { 
        logError("sem mensagens desde ${(now - state.lastMessageReceivedAt)/60000} minutos, reconectando ...");
        keepalive();
    }
    else if (state.lastmessage.contains("ParseError")){
        logError("Problemas no último Parse, reconectando ...");
        keepalive();
    } else {       
        logDebug("Connection Check = ok - Board response. ");
        sendEvent(name: "boardstatus", value: "online")
        runIn(checkInterval, "connectionCheck");
    }
}

def CodigoHEX_ON(final String HEXcode){
    info "CodigoHEX_ON(${HEXcode})"
    state.OnIRsend  = HEXcode
}

def CodigoHEX_OFF(final String HEXcode){
    info "CodigoHEX_OFF(${HEXcode})"
    state.OFFIRsend  = HEXcode
}

def CodigoHEX_mute(final String HEXcode){
    info "CodigoHEX_mute(${HEXcode})"
    state.muteIRsend  = HEXcode
}


def CodigoHEX_source(final String HEXcode){
    info "CodigoHEX_source(${HEXcode})"
    state.sourceIRsend  = HEXcode
}


def CodigoHEX_back(final String HEXcode){
    info "CodigoHEX_back(${HEXcode})"
    state.backIRsend  = HEXcode
}


def CodigoHEX_menu(final String HEXcode){
    info "CodigoHEX_menu(${HEXcode})"
    state.menuIRsend  = HEXcode
}


def CodigoHEX_hdmi1(final String HEXcode){
    info "CodigoHEX_hdmi1(${HEXcode})"
    state.hdmi1IRsend  = HEXcode
}


def CodigoHEX_hdmi2(final String HEXcode){
    info "CodigoHEX_hdmi2(${HEXcode})"
    state.hdmi2IRsend  = HEXcode
}


def CodigoHEX_up(final String HEXcode){
    info "CodigoHEX_up(${HEXcode})"
    state.upIRsend  = HEXcode
}


def CodigoHEX_down(final String HEXcode){
    info "CodigoHEX_down(${HEXcode})"
    state.downIRsend  = HEXcode
}


def CodigoHEX_right(final String HEXcode){
    info "CodigoHEX_right(${HEXcode})"
    state.rightIRsend  = HEXcode
}

def CodigoHEX_left(final String HEXcode){
    info "CodigoHEX_left(${HEXcode})"
    state.leftIRsend  = HEXcode
}

def CodigoHEX_confirm(final String HEXcode){
    info "CodigoHEX_confirm(${HEXcode})"
    state.confirmIRsend  = HEXcode
}


def CodigoHEX_exit(final String HEXcode){
    info "CodigoHEX_exit(${HEXcode})"
    state.exitIRsend  = HEXcode
}


def CodigoHEX_home(final String HEXcode){
    info "CodigoHEX_home(${HEXcode})"
    state.homeIRsend  = HEXcode
}


def CodigoHEX_channelUp(final String HEXcode){
    info "CodigoHEX_channelUp(${HEXcode})"
    state.ChanUpIRsend  = HEXcode
}


def CodigoHEX_channelDown(final String HEXcode){
    info "CodigoHEX_channelDown(${HEXcode})"
    state.ChanDownIRsend  = HEXcode
}


def CodigoHEX_volumeUp(final String HEXcode){
    info "CodigoHEX_volumeUp(${HEXcode})"
    state.VolUpIRsend  = HEXcode
}


def CodigoHEX_volumeDown(final String HEXcode){
    info "CodigoHEX_volumeDown(${HEXcode})"
    state.VolDownIRsend  = HEXcode
}


def CodigoHEX_num0(final String HEXcode){
    info "CodigoHEX_num0(${HEXcode})"
    state.num0IRsend  = HEXcode
}


def CodigoHEX_num1(final String HEXcode){
    info "CodigoHEX_num1(${HEXcode})"
    state.num1IRsend  = HEXcode
}


def CodigoHEX_num2(final String HEXcode){
    info "CodigoHEX_num2(${HEXcode})"
    state.num2IRsend  = HEXcode
}


def CodigoHEX_num3(final String HEXcode){
    info "CodigoHEX_num3(${HEXcode})"
    state.num3IRsend  = HEXcode
}

def CodigoHEX_num4(final String HEXcode){
    info "CodigoHEX_num4(${HEXcode})"
    state.num4IRsend  = HEXcode
}

def CodigoHEX_num5(final String HEXcode){
    info "CodigoHEX_num5(${HEXcode})"
    state.num5IRsend  = HEXcode
}

def CodigoHEX_num6(final String HEXcode){
    info "CodigoHEX_num6(${HEXcode})"
    state.num6IRsend  = HEXcode
}

def CodigoHEX_num7(final String HEXcode){
    info "CodigoHEX_num7(${HEXcode})"
    state.num7IRsend  = HEXcode
}

def CodigoHEX_num8(final String HEXcode){
    info "CodigoHEX_num8(${HEXcode})"
    state.num8IRsend  = HEXcode
}

def CodigoHEX_num9(final String HEXcode){
    info "CodigoHEX_num9(${HEXcode})"
    state.num9IRsend  = HEXcode
}

def CodigoHEX_btnextra1(final String HEXcode){
    info "CodigoHEX_btnextra1(${HEXcode})"
    state.btnextra1IRsend  = HEXcode
}

def CodigoHEX_btnextra2(final String HEXcode){
    info "CodigoHEX_btnextra2(${HEXcode})"
    state.btnextra2IRsend  = HEXcode
}

def CodigoHEX_btnextra3(final String HEXcode){
    info "CodigoHEX_btnextra3(${HEXcode})"
    state.btnextra3IRsend  = HEXcode
}

def CodigoHEX_APP_NETFLIX(final String HEXcode){
    info "CodigoHEX_appnetflix(${HEXcode})"
    state.netflixIRsend  = HEXcode
}

def CodigoHEX_APP_AMAZON(final String HEXcode){
    info "CodigoHEX_appamazon(${HEXcode})"
    state.amazonIRsend  = HEXcode
}

def CodigoHEX_APP_YOUTUBE(final String HEXcode){
    info "CodigoHEX_appyoutube(${HEXcode})"
    state.youtubeIRsend  = HEXcode
}




private EnviaComando(s) {
    logDebug("sendingCommand ${s}")
    interfaces.rawSocket.sendMessage(s)    
}


//Basico on e off para Switch 
def on() {
     sendEvent(name: "switch", value: "on", isStateChange: true)
     def ircode =  state.OnIRsend    
     EnviaComando(ircode)    

}

def off() {
     sendEvent(name: "switch", value: "off", isStateChange: true)
     def ircode =  state.OFFIRsend    
     EnviaComando(ircode)
         
}


def parse(msg) {
    state.lastMessageReceived = new Date(now()).toString();
    state.lastMessageReceivedAt = now();
        
    def oldmessage = state.lastmessage
    
    def newmsg = hubitat.helper.HexUtils.hexStringToByteArray(msg) //na Mol, o resultado vem em HEX, então preciso converter para Array
    def newmsg2 = new String(newmsg) // Array para String    
    
    state.lastmessage = newmsg2 //ok
    larguramsg = newmsg2.length()
    state.larguramsg = larguramsg
    log.info "qtde chars = " + larguramsg
    log.info "lastmessage = " + newmsg2
    
    //completeir,1:8,1
    //qtde chars = 18

    //IR Return 
    if ((newmsg2.contains("completeir") && (newmsg2.length() == 18))) {
        //log.info "mac completa = " + newmsg2

                
        log.info "Enviado OK o Comando IR "
        sendEvent(name: "boardstatus", value: "online")

    }    
    
    
    
    
    

}


//Case para los botones de push en el dashboard. 
def push(pushed) {
	logDebug("push: button = ${pushed}")
	if (pushed == null) {
		logWarn("push: pushed is null.  Input ignored")
		return
	}
	pushed = pushed.toInteger()
	switch(pushed) {
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
		case 41: poweroff(); break            
		default:
			logDebug("push: Botão inválido.")
			break
	}
}

//Botão #1 para dashboard
def poweron(){
     sendEvent(name: "switch", value: "on", isStateChange: true)
     def ircode =  state.OnIRsend 
     EnviaComando(ircode)  
}

//Botão #41 para dashboard
def poweroff(){
     sendEvent(name: "switch", value: "off", isStateChange: true)
     def ircode =  state.OffIRsend 
     EnviaComando(ircode)  
}


//Botão #2 para dashboard
def mute(){
	sendEvent(name: "volume", value: "mute")
    def ircode =  state.muteIRsend
    EnviaComando(ircode)    
}



//Botão #3 para dashboard
def source(){
	sendEvent(name: "action", value: "source")
    def ircode =  state.sourceIRsend
    EnviaComando(ircode)    
}

//Botão #4 para dashboard
def back(){
	sendEvent(name: "action", value: "back")
    def ircode = state.backIRsend
    EnviaComando(ircode)    
}

//Botão #5 para dashboard
def menu(){
	sendEvent(name: "action", value: "menu")
    def ircode =  state.menuIRsend
    EnviaComando(ircode)    
}


//Botão #6 para dashboard
def hdmi1(){
    sendEvent(name: "input", value: "hdmi1")
    def ircode =   state.hdmi1IRsend
    EnviaComando(ircode)
}

//Botão #7 para dashboard
def hdmi2(){
    sendEvent(name: "input", value: "hdmi2")
    def ircode =  state.hdmi2IRsend 
    EnviaComando(ircode)
}



//Botão #8 para dashboard
def left(){
    sendEvent(name: "action", value: "left")
    def ircode =  state.btnextra1IRsend 
    EnviaComando(ircode)
}

//Botão #9 para dashboard
def right(){
    sendEvent(name: "action", value: "right")
     def ircode =  state.btnextra1IRsend 
    EnviaComando(ircode)
}



//Botão #10 para dashboard
def up(){
    sendEvent(name: "action", value: "up")
    def ircode =  state.upIRsend
    EnviaComando(ircode)
}

//Botão #11 para dashboard
def down(){
    sendEvent(name: "action", value: "down")
    def ircode =  state.hdmi1
    EnviaComando(ircode)
}

//Botão #12 para dashboard
def confirm(){
    sendEvent(name: "action", value: "confirm")
    def ircode =  state.confirmIRsend
    EnviaComando(ircode)
}


//Botão #13 para dashboard
def exit(){
	sendEvent(name: "action", value: "exit")
    def ircode =  state.exitIRsend
    EnviaComando(ircode)    
}




//Botão #14 para dashboard
def home(){
    sendEvent(name: "action", value: "home")
    def ircode =  state.homeIRsend 
    EnviaComando(ircode)
}



//Botão #18 para dashboard
def channelUp(){
	sendEvent(name: "channel", value: "chup")
   def ircode =  state.ChanUpIRsend 
    EnviaComando(ircode)    
}

//Botão #19 para dashboard
def channelDown(){
	sendEvent(name: "channel", value: "chdown")
    def ircode =  state.ChanDownIRsend 
    EnviaComando(ircode)    
}

//Botão #21 para dashboard
def volumeUp(){
	sendEvent(name: "volume", value: "volup")
    def ircode =  state.VolUpIRsend
    EnviaComando(ircode)    
    
}

//Botão #22 para dashboard
def volumeDown(){
	sendEvent(name: "volume", value: "voldown")
    def ircode =  state.VolDownIRsend
    EnviaComando(ircode)    
}


//Botão #23 para dashboard
def num0(){
    sendEvent(name: "action", value: "num0")
    def ircode =  state.num0IRsend 
    EnviaComando(ircode)
}

//Botão #24 para dashboard
def num1(){
    sendEvent(name: "action", value: "num1")
   def ircode =  state.num1IRsend 
    EnviaComando(ircode)
}

//Botão #25 para dashboard
def num2(){
    sendEvent(name: "action", value: "num2")
    def ircode =  state.num2IRsend 
    EnviaComando(ircode)
}


//Botão #26 para dashboard
def num3(){
    sendEvent(name: "action", value: "num3")
    def ircode =  state.num3IRsend 
    EnviaComando(ircode)
}

//Botão #27 para dashboard
def num4(){
    sendEvent(name: "action", value: "num4")
    def ircode =  state.num4IRsend 
    EnviaComando(ircode)
}

//Botão #28 para dashboard
def num5(){
    sendEvent(name: "action", value: "num5")
    def ircode =  state.num5IRsend 
    EnviaComando(ircode)
}

//Botão #29 para dashboard
def num6(){
    sendEvent(name: "action", value: "num6")
    def ircode =  state.num6IRsend 
    EnviaComando(ircode)
}


//Botão #30 para dashboard
def num7(){
    sendEvent(name: "action", value: "num7")
    def ircode =  state.num7IRsend 
    EnviaComando(ircode)
}

//Botão #31 para dashboard
def num8(){
    sendEvent(name: "action", value: "num8")
    def ircode =  state.num8IRsend 
    EnviaComando(ircode)
}

//Botão #32 para dashboard
def num9(){
    sendEvent(name: "action", value: "num9")
    def ircode =  state.num9IRsend 
    EnviaComando(ircode)
}

//Botão #33 para dashboard
def btnextra1(){
    sendEvent(name: "action", value: "confirm")
    def ircode =  state.btnextra1IRsend 
    EnviaComando(ircode)
}

//Botão #34 para dashboard
def btnextra2(){
    sendEvent(name: "action", value: "btnextra2")
    def ircode =  state.btnextra2IRsend 
    EnviaComando(ircode)
}

//Botão #35 para dashboard
def btnextra3(){
    sendEvent(name: "action", value: "btnextra3")
    def ircode =  state.btnextra3IRsend 
    EnviaComando(ircode)
}

//Botão #38 para dashboard
def appAmazonPrime(){
    sendEvent(name: "input", value: "amazon")
    def ircode =  state.amazonIRsend 
    EnviaComando(ircode)
}

//Botão #39 para dashboard
def appyoutube(){
    sendEvent(name: "input", value: "youtube")
   def ircode =  state.youtubeIRsend 
    EnviaComando(ircode)
}


//Botão #40 para dashboard
def appnetflix(){
    sendEvent(name: "input", value: "netflix")
    def ircode =  state.netflixIRsend 
    EnviaComando(ircode)
}


def logsOff() {
    log.warn 'logging disabled...'
    device.updateSetting('logInfo', [value:'false', type:'bool'])
    device.updateSetting('logWarn', [value:'false', type:'bool'])
    device.updateSetting('logDebug', [value:'false', type:'bool'])
    device.updateSetting('logTrace', [value:'false', type:'bool'])
}

void logDebug(String msg) {
    if ((Boolean)settings.logDebug != false) {
        log.debug "${drvThis}: ${msg}"
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



def info(msg) {
    if (logLevel == "INFO" || logLevel == "DEBUG") {
        log.info(msg)
    }
}
