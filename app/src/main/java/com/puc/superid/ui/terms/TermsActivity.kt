package com.puc.superid.ui.terms

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.puc.superid.ui.theme.SuperidTheme

class TermsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SuperidTheme {
                TermsScreen(this)
            }
        }
    }
}

@Composable
fun TermsScreen(activity: TermsActivity) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = getTermsOfUse(),
                fontSize = 16.sp,
                lineHeight = 24.sp
            )
            Button(onClick = { activity.finish() },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF00EECA),
                    contentColor = Color.White
                ))
                { Text("Fechar")
            }
        }
    }
}

fun getTermsOfUse(): String {
    return """
                     TERMOS DE USO - SUPERID

1. INTRODUÇÃO
Bem-vindo ao SuperID! O SuperID é um gerenciador de autenticação que permite armazenar senhas com segurança e realizar login sem senha em sites parceiros via QR Code. Ao utilizar o aplicativo SuperID, você concorda integralmente com os presentes Termos de Uso. Leia atentamente antes de prosseguir com o uso do aplicativo.

2. CADASTRO E CONTA
- Para utilizar o SuperID, é necessário criar uma conta fornecendo nome, e-mail e senha mestre.
- O e-mail informado deve ser válido e será utilizado para recuperação de senha.
- Você é o único responsável por manter sua senha mestre segura.
- O compartilhamento de conta com terceiros é proibido.

3. ARMAZENAMENTO E PROCESSAMENTO DE DADOS PESSOAIS
- O SuperID coleta e processa dados pessoais necessários para a prestação dos serviços, incluindo nome, e-mail, senhas criptografadas, logs de acesso e informações sobre dispositivos utilizados.
- As senhas armazenadas no SuperID são criptografadas e não podem ser acessadas pela empresa.
- Podemos utilizar dados anonimizados para melhoria do serviço e análise de uso.
- Os dados serão armazenados em servidores seguros e podem ser transferidos entre diferentes regiões para garantir disponibilidade e redundância.

4. PERMISSÕES E ACESSO A RECURSOS DO APARELHO
Para que o SuperID funcione corretamente, poderemos solicitar acesso a:
- Câmera: Necessária para leitura de QR Codes para login sem senha.
- Rede e conexão com a internet: Para sincronização de dados e acesso a serviços na nuvem.
- Armazenamento: Para salvar credenciais criptografadas localmente.
- Notificações: Para alertas sobre tentativas de login e outras informações de segurança.
- Identificadores de dispositivo: Para evitar acessos indevidos e garantir a segurança da conta.

5. LOGIN SEM SENHA VIA QR CODE
- O SuperID permite autenticação em sites parceiros sem necessidade de senha.
- O login ocorre através da leitura de um QR Code gerado pelo site parceiro e confirmada no aplicativo SuperID.
- O processo envolve a transmissão segura de dados para validação do login.
- O SuperID não se responsabiliza por acessos indevidos caso seu dispositivo seja comprometido.

6. SEGURANÇA E RESPONSABILIDADES
- O usuário se compromete a manter seu dispositivo seguro e protegido contra acessos não autorizados.
- O compartilhamento da senha mestre ou QR Codes de autenticação com terceiros é de inteira responsabilidade do usuário.
- O SuperID adota medidas de segurança para proteção dos dados, mas não pode garantir segurança absoluta contra ataques cibernéticos.
- O uso indevido do aplicativo que comprometa a segurança de terceiros pode resultar na suspensão ou exclusão da conta.

7. LIMITAÇÃO DE RESPONSABILIDADE
- O SuperID não se responsabiliza por perdas, danos ou prejuízos decorrentes do uso do aplicativo, incluindo acessos indevidos resultantes de negligência do usuário.
- O aplicativo é fornecido "como está" e não garantimos sua disponibilidade ininterrupta.

8. MODIFICAÇÕES NOS TERMOS DE USO
- O SuperID pode modificar estes Termos de Uso a qualquer momento.
- É responsabilidade do usuário revisar os termos periodicamente.
- O uso contínuo do aplicativo após alterações nos Termos implica na concordância com as novas condições.

9. EXCLUSÃO DA CONTA E DADOS
- O usuário pode solicitar a exclusão da conta e dos dados pessoais armazenados a qualquer momento.
- Para solicitar a exclusão, entre em contato pelo e-mail suporte@superid.com.
- Algumas informações podem ser retidas por obrigações legais.

10. CONTATO
- Em caso de dúvidas, suporte ou solicitações sobre privacidade, entre em contato pelo e-mail suporte@superid.com.

Ao utilizar o SuperID, você declara que leu, compreendeu e concorda integralmente com estes Termos de Uso.
        
    """.trimIndent()
}