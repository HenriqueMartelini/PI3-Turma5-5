# PI3-Turma5-5

Visão Geral

SuperID é um aplicativo de gerenciamento de autenticações que permite aos usuários
armazenar senhas de forma segura, realizar login sem senha via QR Code e gerenciar suas credenciais
de forma prática e eficiente. A solução visa aumentar a segurança e a conveniência no gerenciamento
de acessos.


Pré-requisitos
•	JDK 11 ou superior instalado.
•	Android Studio instalado.
•	Conta no Firebase para gerar as credenciais e configurar o Firebase no projeto.

Passos para testar o código:
Passo 1: Clonar o repositório

Clone o repositório para o seu ambiente local:
git clone https://github.com/HenriqueMartelini/PI3-Turma5-5.git

Passo 2: Instalar as dependências

Acesse o diretório do projeto e execute o seguinte comando para baixar todas as dependências:
./gradlew build
Se você estiver utilizando o Android Studio, ao abrir o projeto,
o Gradle irá automaticamente baixar as dependências necessárias.

Passo 3: Rodar o app em um emulador

Passos para testar o APP:
Passo1: Instalar o APK em um dspositivo android:
https://drive.google.com/file/d/1zPx6l1zxwoGc9PQs6S7kq4r4KgOwxNBP/view?usp=sharing
Cada dispostivo exige um passo a passo de instalação de arquivos APK, mas em suma, ele pedirá um destino
onde deseja copiar o APK, e então, acesse essa pasta, clique sobre o APK e isntale o APP. Talvez o dispositivo
classifique o APP como risco eminente para o dispoitivo, e você terá de aceitar, mudar as configurações para aceitar
o dowloand do mesmo, que assume os riscos. Após isto, o app será instalado e você poderá usá-lo normalmente. 

Passo 2: Criar uma conta usando um email válido, após isto você terá de verificar seu email, caso ele não chegue,
use a função de reenviar o email. E então, você poderá logar e usar o APP à vontade.

Passo 3: Acesse, clone e siga os passos do repositório:
https://github.com/HenriqueMartelini/PartnerWebSite-PI3-Turma5-5.git

### **Configuração do Firebase**

Para configurar o Firebase no seu ambiente local, siga as etapas abaixo:

1. Faça o download do arquivo `google-services.json` do Firebase:
    - Clique [aqui](https://drive.google.com/file/d/1HMO2AM-AydVjUsknB8GWvUQwTRwYcF4p/view?usp=sharing) para acessar o arquivo.

2. Coloque o arquivo `google-services.json` na pasta `app/` do seu projeto.

3. Sincronize o projeto no Android Studio. O Firebase estará configurado automaticamente.


Tecnologias Utilizadas

•  O projeto foi desenvolvido utilizando as seguintes tecnologias:

•  Linguagem: Kotlin

•  Framework: Jetpack Compose (para UI)

•  Autenticação e Banco de Dados: Firebase Authentication e Firebase Firestore

•  Armazenamento Seguro: EncryptedSharedPreferences e Firebase Cloud Firestore

•  Arquitetura: Model-View-ViewModel (MVVM)

•  Controle de Versão: Git/GitHub

•  Gerenciamento de Dependências: Gradle


Fluxo de Desenvolvimento

O projeto segue um fluxo de desenvolvimento baseado no Git Flow:

1. main: Branch principal, onde ficará a versão estável do projeto. Alterações diretas nesta branch são evitadas.

2. develop: Branch de desenvolvimento, onde novas funcionalidades são integradas antes de serem enviadas para a main.

3. feature/nome-da-funcionalidade: Cada nova funcionalidade deve ser desenvolvida em uma branch específica,
derivada da develop. Após finalização, ela será integrada novamente na develop.