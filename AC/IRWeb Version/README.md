# CA 7Port + Hubitat Driver para IR - AC - usando irweb
1era Versão do Driver para usar Hubitat + 7port da CA. 

Driver para usar Controles Remotos criados no http://ir.molsmart.com.br. 

1. Criar o seu usuário e senha no http://ir.molsmart.com.br.
2. Criar um novo, ou importar um controle remoto compartilhado.
3. Instalar o Driver manualmente na sua Hubitat (irweb para AC ou irweb para TV) .
4. Criar um Virtual Device, do tipo "ControlArt - 7port - IR - AC(irweb)" 
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



# Relação de botões para Controles de Ar Condicionado   

		Botão 2 : auto
		Botão 3 : heat
		Botão 4 : cool
        	Botão 5 : fan
       		Botão 6 : dry
        	Botão 7 : setautocool                
        	Botão 8 : comandoextra1    
        	Botão 9 : comandoextra2            
        	Botão 10 : comandoextra3            
        	Botão 11 : comandoextra4    
        	Botão 12 : comandoextra5    
        	Botão 13 : fanAuto    
        	Botão 14 : fanLow    
        	Botão 15 : fanMed    
       		Botão 16 : fanHigh   
        	Botão 17 : comandoextra6  
        	Botão 18 : comandoextra7  
        	Botão 19 : comandoextra8   
		Botão 20 : fastcold
		Botão 21 : temp18
		Botão 22 : temp20
		Botão 23 : temp22
		Botão 24 : clock
		Botão 25 : sweep
		Botão 26 : turbo
		Botão 27 : fan
		Botão 28 : temp17
		Botão 29 : temp23
		Botão 30 : temp26
		Botão 31 : onoff
		Botão 32 : temp19
		Botão 33 : temp21
		Botão 34 : swing
		Botão 35 : manual
		Botão 36 : mode
		Botão 37 : up
		Botão 38 : timer
		Botão 39 : cancel
		Botão 40 : down
		Botão 41 : display
		Botão 42 : io
		Botão 43 : tempup
		Botão 44 : tempdown
		Botão 45 : fanspeed
		Botão 46 : poweroff
		Botão 47 : poweron
  
