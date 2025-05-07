# CA 7Port + Hubitat Driver para IR - TV - usando irweb
1era Versão do Driver para usar Hubitat + 7port da CA. 

Driver para usar Controles Remotos criados no http://ir.molsmart.com.br. 

1. Criar o seu usuário e senha no http://ir.molsmart.com.br.
2. Criar um novo, ou importar um controle remoto compartilhado.
3. Instalar o Driver manualmente na sua Hubitat (irweb para AC ou irweb para TV) .
4. Criar um Virtual Device, do tipo "ControlArt - 7port - IR - TV(irweb)" 
5. Inserir todos os dados do seu 7Port (Ip, porta).
6. Voltar para o Site do Controle IR, e copiar o endereço URL do controle remoto para compartir. (ex: https://molsmart-integration.web.app/controle-integration?token=e7eca569-67a8-4176-9ebc-b6d6d84892b7ó ) - sem acentos. 

   ![image](https://github.com/user-attachments/assets/6c4847cf-3cbe-410f-85d9-822dc9a4e5e4)
7. Colar o link do controle, no input no driver na parte de preferenças.

![image](https://github.com/user-attachments/assets/669cd741-d543-4188-b394-19102c844f60)

8. Executar o comando "GetRemoteData" dentro da página do Driver e deveria receber o State = "Sucess". Os comandos foram criados dentro do device.

   ![image](https://github.com/user-attachments/assets/fd05dd73-af69-4b7c-a172-41da9c36a222)

9. Se precisar alterar algum comando IR do controle, pode voltar no site IR MolSmart, entrar no controle remoto e salvar o comando novo. Após ter feito isso, é só repetir o passo #8 para trazer as informações
    atualizadas do controle para dentro do seu device. 


- Pode usar o Device, pode usar como Botões no seu dashboard. Adicionar o device no dashboard. Embaixo a relação e numeros para cada botão. 


# Relação de botões para Controles de TV   

		Botão 1 : poweron
		Botão 2 : mute
		Botão 3 : source
		Botão 4 : back
		Botão 5 : menu
		Botão 6 : hdmi1
		Botão 7 : hdmi2
		Botão 8 : left
		Botão 9 : right
		Botão 10: up
		Botão 11: down
		Botão 12: confirm
		Botão 13: exit
		Botão 14: home
		Botão 18: channelUp
		Botão 19: channelDown
		Botão 21: volumeUp
		Botão 22: volumeDown
		Botão 23: num0
		Botão 24: num1
		Botão 25: num2
		Botão 26: num3
		Botão 27: num4
		Botão 28: num5
		Botão 29: num6
		Botão 30: num7
		Botão 31: num8
		Botão 32: num9
		Botão 33: btnextra1
		Botão 34: btnextra2
		Botão 35: btnextra3
		Botão 38: appAmazonPrime
		Botão 39: appYouTube
		Botão 40: appNetflix
		Botão 41: btnextra4
		Botão 42: btnextra5
		Botão 43: btnextra6
		Botão 44: btnextra7
		Botão 45: btnAIRsend
		Botão 46: btnBIRsend
		Botão 47: btnCIRsend
		Botão 48: btnDIRsend
		Botão 49: playIRsend
		Botão 50: pauseIRsend
		Botão 51: nextIRsend
		Botão 52: guideIRsend
		Botão 53: infoIRsend
		Botão 54: toolsIRsend
		Botão 55: smarthubIRsend
		Botão 56: previouschannelIRsend
		Botão 57: backIRsend
  	 	Botão 58: poweroff

