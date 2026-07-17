/**
 *  ControlArt 7port Driver - RF - Cortinas (Subir / Parar / Descer)
 *
 *  Copyright 2026 VH
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
 *   --- Driver para 7Port - RF - Cortinas ---
 *   Conexão TCP (rawSocket) baseada no driver "ControlArt - 7port - IR para TV e SOM".
 *   Botões Subir/Parar/Descer e Child Buttons baseados no driver "MolSmart - GW8 - RF".
 *
 *   As 3 strings de comando RF (ex.: "sendrf,2,1,1,900,1800,...") são coladas pelo usuário
 *   nas Preferences do driver, uma para cada botão (Subir, Parar, Descer).
 *
 *   +++  Versões ++++
 *        1.0 - 17/7/2026 - V1
 *
 */

import groovy.transform.Field

@Field static final String DRIVER_VERSION = "1.0"
@Field static final String DRIVER = "by TRATO"
@Field static final String USER_GUIDE = "https://github.com/hhorigian/Controlart_7port"
@Field static final Integer checkInterval = 150

metadata {
  definition (name: "ControlArt - 7port - RF - Cortinas", namespace: "TRATO", author: "VH", vid: "generic-contact") {
    capability "Sensor"
    capability "Actuator"
    capability "Contact Sensor"
    capability "PushableButton"
    capability "Initialize"

    command "up"
    command "down"
    command "stop"
    command "Up"
    command "Down"
    command "Stop"
    command "open"
    command "close"
    command "stopPositionChange"

    command "reconnect"
    command "keepalive"
    command "refresh"
    command "recreateButtons"

    command "CodigoRF_up", [
        [name: "RFcode", type: "STRING", description: "SUBIR/ABRIR = COLAR O CODIGO SendRF"]
    ]
    command "CodigoRF_stop", [
        [name: "RFcode", type: "STRING", description: "PARAR = COLAR O CODIGO SendRF"]
    ]
    command "CodigoRF_down", [
        [name: "RFcode", type: "STRING", description: "DESCER/FECHAR = COLAR O CODIGO SendRF"]
    ]

    attribute "boardstatus", "string"
    attribute "status", "string"
    attribute "currentstatus", "string"
    attribute "driverVersion", "string"
  }

  preferences {
    input name: "device_IP_address", type: "text",   title: "IP Address do 7Port",  required: true
    input name: "device_port",       type: "number", title: "Porta do 7Port",       required: true, defaultValue: 4998

    input name: "createButtonsOnSave", type: "bool", title: "Criar/atualizar Child Buttons ao salvar", defaultValue: true

    input name: "logInfo",  type: "bool", title: "Show Info Logs?",              defaultValue: true
    input name: "logWarn",  type: "bool", title: "Show Warning Logs?",           defaultValue: true
    input name: "logDebug", type: "bool", title: "Show Debug Logs?",             defaultValue: false
    input name: "logTrace", type: "bool", title: "Show Detailed Logs?",          defaultValue: false

    input name: "UserGuide", type: "hidden", title: fmtHelpInfo("Manual do Driver")
  }
}

String fmtHelpInfo(String str) {
  String prefLink = "<a href='${USER_GUIDE}' target='_blank'>${str}<br><div style='font-size: 70%;'>${DRIVER}</div></a>"
  return "<div style='font-size: 160%; font-style: bold; padding: 2px 0px; text-align: center;'>${prefLink}</div>"
}

/* ======================= Setup ======================= */

def installed() {
  logTrace('installed()')
  sendEvent(name: "numberOfButtons", value: 3)
  sendEvent(name: "status", value: "stop")
  sendEvent(name: "boardstatus", value: "offline")
  sendEvent(name: "driverVersion", value: DRIVER_VERSION)
  runIn(1800, logsOff)
  initialize()
}

def updated() {
  logTrace('updated()')
  sendEvent(name: "numberOfButtons", value: 3)
  sendEvent(name: "driverVersion", value: DRIVER_VERSION)
  initialize()
}

def uninstalled() {
  logTrace('uninstalled()')
  unschedule()
  try { interfaces.rawSocket.close() } catch (ignored) { }
}

def initialize() {
  unschedule()
  logTrace('Run Initialize()')
  try { interfaces.rawSocket.close() } catch (ignored) { }

  if (!device_IP_address) {
    logError 'IP do Device não configurado'
    return
  }
  if (!device_port) {
    logError 'Porta do Device não configurada.'
    return
  }

  try {
    logTrace("Initialize: Tentando conexão com o device no ${device_IP_address}...na porta configurada: ${device_port}")
    interfaces.rawSocket.connect(device_IP_address, (int) device_port)
    state.lastMessageReceivedAt = now()
    sendEvent(name: "boardstatus", value: "online", isStateChange: true)
    runIn(checkInterval, "connectionCheck")
  } catch (e) {
    logError("Initialize: com ${device_IP_address} com um error: ${e.message}")
    sendEvent(name: "boardstatus", value: "offline", isStateChange: true)
    runIn(60, "initialize")
  }

  if (createButtonsOnSave) createOrUpdateChildButtons(true)
}

def reconnect() {
  try { interfaces.rawSocket.close() } catch (ignored) { }
  state.lastMessageReceived = ""
  state.lastmessage = ""

  try {
    logTrace("tentando conexão com o device no ${device_IP_address}...na porta ${device_port}")
    interfaces.rawSocket.connect(device_IP_address, (int) device_port)
    state.lastMessageReceivedAt = now()
    sendEvent(name: "boardstatus", value: "online", isStateChange: true)
    runIn(checkInterval, "connectionCheck")
  } catch (e) {
    logError("${device_IP_address} reconnect error: ${e.message}")
    sendEvent(name: "boardstatus", value: "offline", isStateChange: true)
  }
}

def keepalive() {
  logTrace('keepalive()')
  reconnect()
}

def refresh() {
  // Não envia nenhum comando de movimento (evita mover a cortina sem querer) -
  // apenas garante/testa a conexão TCP com o 7Port.
  logTrace('refresh() -> testando conexão')
  reconnect()
}

def socketStatus(status) {
  logDebug("Socket status: ${status}")
  if (status?.contains("error") || status?.contains("Broken pipe")) {
    logWarn("Socket error detectado, reconectando...")
    sendEvent(name: "boardstatus", value: "offline", isStateChange: true)
    runIn(5, "reconnect")
  }
}

def connectionCheck() {
  def nowMs = now()
  if (!state.lastMessageReceivedAt || (nowMs - state.lastMessageReceivedAt) > (checkInterval * 1000)) {
    logWarn("sem mensagens desde ${(nowMs - (state.lastMessageReceivedAt ?: nowMs))/60000} minutos, reconectando ...")
    reconnect()
  } else {
    logDebug("Connection Check = ok - Board response.")
    sendEvent(name: "boardstatus", value: "online")
    runIn(checkInterval, "connectionCheck")
  }
}

def parse(msg) {
  state.lastMessageReceivedAt = now()
  try {
    def newmsg = hubitat.helper.HexUtils.hexStringToByteArray(msg)
    def newmsg2 = new String(newmsg)
    state.lastmessage = newmsg2
    logDebug("Recebido: ${newmsg2}")
  } catch (e) {
    logDebug("parse: não foi possível decodificar a mensagem (${e.message})")
  }
  sendEvent(name: "boardstatus", value: "online")
}

/* ======================= Configuração dos códigos RF ======================= */
/* As strings de RF (ex.: "sendrf,2,1,1,900,1800,...") podem passar de 5000
 * caracteres, e as Preferences do Hubitat têm limite de tamanho para inputs
 * de texto. Por isso, assim como no driver de IR (CodigoHEX_xxx), os códigos
 * são colados via Command (STRING) e guardados em state, não em settings.   */

def CodigoRF_up(final String RFcode) {
  logInfo "CodigoRF_up(${RFcode})"
  state.upRFsend = RFcode
}

def CodigoRF_stop(final String RFcode) {
  logInfo "CodigoRF_stop(${RFcode})"
  state.stopRFsend = RFcode
}

def CodigoRF_down(final String RFcode) {
  logInfo "CodigoRF_down(${RFcode})"
  state.downRFsend = RFcode
}

/* ======================= Comandos ======================= */

def up()    { EnviaComando(state.upRFsend,   "Subir");  sendEvent(name: "status", value: "up");   sendEvent(name: "currentstatus", value: "up") }
def Up()    { up() }
def open()  { up() }

def stop()  { EnviaComando(state.stopRFsend, "Parar");  sendEvent(name: "status", value: "stop"); sendEvent(name: "currentstatus", value: "stop") }
def Stop()  { stop() }
def stopPositionChange() { stop() }

def down()  { EnviaComando(state.downRFsend, "Descer"); sendEvent(name: "status", value: "down"); sendEvent(name: "currentstatus", value: "down") }
def Down()  { down() }
def close() { down() }

def push(number) {
  logDebug("push: button = ${number}")
  if (number == null) {
    logWarn("push: pushed is null. Input ignorado")
    return
  }
  sendEvent(name: "pushed", value: number, isStateChange: true)
  switch (number.toInteger()) {
    case 1: up();   break
    case 2: stop(); break
    case 3: down(); break
    default: logDebug("push: Botão inválido.")
  }
}

/* ======================= Envio TCP (RF) ======================= */

private void EnviaComando(String code, String label = "") {
  if (!code?.trim()) {
    logWarn("Código RF para '${label}' não configurado. Use o command CodigoRF_up / CodigoRF_stop / CodigoRF_down para colar o código SendRF correspondente.")
    return
  }
  logDebug("Enviando RF (${label}): ${code}")
  try {
    interfaces.rawSocket.sendMessage(code)
  } catch (e) {
    logWarn("Falha ao enviar comando RF (${label}): ${e.message}")
  }
}

/* ======================= CHILD BUTTONS (Subir / Parar / Descer) ======================= */

@Field static final List<Map> CHILD_BUTTON_DEFS = [
  [prefix: "Subir Cortina",  cmd: "up"],
  [prefix: "Parar Cortina",  cmd: "stop"],
  [prefix: "Descer Cortina", cmd: "down"]
]

private String buildChildLabel(String prefix) {
  String parentLabel = device?.getLabel() ?: device?.getName() ?: "7Port RF"
  return "${prefix} ${parentLabel}".trim()
}

def recreateButtons() { createOrUpdateChildButtons(true) }

private void createOrUpdateChildButtons(Boolean removeExtras = false) {
  logDebug("Criando/atualizando Child Buttons...")
  Set<String> keep = []
  CHILD_BUTTON_DEFS.eachWithIndex { m, idx ->
    String dni = "${device.id}-BTN-${idx + 1}"
    String childLabel = buildChildLabel(m.prefix as String)
    def child = getChildDevice(dni)
    if (!child) {
      child = addChildDevice("hubitat", "Generic Component Switch", dni,
        [name: childLabel, label: childLabel, isComponent: true])
      logDebug("Child criado: ${child?.displayName}")
    } else {
      if (child.label != childLabel) child.setLabel(childLabel)
    }
    child.updateDataValue("cmd", m.cmd as String)
    try { child.parse([[name: "switch", value: "off"]]) } catch (ignored) { }
    keep << dni
  }
  if (removeExtras) {
    childDevices?.findAll { !(it.deviceNetworkId in keep) }?.each {
      logWarn("Removendo child extra: ${it.displayName}")
      deleteChildDevice(it.deviceNetworkId)
    }
  }
}

def componentOn(cd)  { handleChildPress(cd) }
def componentOff(cd) { /* ignorar */ }

private void handleChildPress(cd) {
  String cmd = cd.getDataValue("cmd") ?: ""
  logDebug("Child '${cd.displayName}' acionado -> cmd=${cmd}")
  switch (cmd) {
    case "up":   up();   break
    case "stop": stop(); break
    case "down": down(); break
    default: logWarn("Child ${cd.displayName} com cmd desconhecido: ${cmd}")
  }
  runIn(1, "childOffSafe", [data: [dni: cd.deviceNetworkId]])
}

def childOffSafe(data) {
  def child = getChildDevice(data?.dni as String)
  if (child) {
    try { child.parse([[name: "switch", value: "off"]]) } catch (ignored) { }
  }
}

/* ======================= Util ======================= */

def logsOff() {
  log.warn 'logging disabled...'
  device.updateSetting('logDebug', [value: 'false', type: 'bool'])
  device.updateSetting('logTrace', [value: 'false', type: 'bool'])
}

void logDebug(String msg) { if ((Boolean)settings.logDebug != false) log.debug "${device.displayName}: ${msg}" }
void logInfo(String msg)  { if ((Boolean)settings.logInfo  != false) log.info  "${device.displayName}: ${msg}" }
void logTrace(String msg) { if ((Boolean)settings.logTrace != false) log.trace "${device.displayName}: ${msg}" }
void logWarn(String msg, boolean force = false) { if (force || (Boolean)settings.logWarn != false) log.warn "${device.displayName}: ${msg}" }
void logError(String msg) { log.error "${device.displayName}: ${msg}" }
