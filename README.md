# PI3-Turma5-5

Pré-requisitos
•	JDK 11 ou superior instalado.
•	Android Studio instalado.
•	Conta no Firebase para gerar as credenciais e configurar o Firebase no projeto.

Passo 1: Clonar o repositório

Clone o repositório para o seu ambiente local:
git clone https://github.com/HenriqueMartelini/PI3-Turma5-5.git

Passo 2: Instalar as dependências

Acesse o diretório do projeto e execute o seguinte comando para baixar todas as dependências:
./gradlew build
Se você estiver utilizando o Android Studio, ao abrir o projeto,
o Gradle irá automaticamente baixar as dependências necessárias.


### **Configuração do Firebase**

Para configurar o Firebase no seu ambiente local, siga as etapas abaixo:

1. Faça o download do arquivo `google-services.json` do Firebase:
    - Clique [aqui](https://drive.google.com/file/d/1HMO2AM-AydVjUsknB8GWvUQwTRwYcF4p/view?usp=sharing) para acessar o arquivo.

2. Coloque o arquivo `google-services.json` na pasta `app/` do seu projeto.

3. Sincronize o projeto no Android Studio. O Firebase estará configurado automaticamente.