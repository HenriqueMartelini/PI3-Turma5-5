package com.puc.superid.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.puc.superid.R
import com.puc.superid.ui.registration.SignUpActivity
import com.puc.superid.ui.terms.TermsActivity
import kotlinx.coroutines.launch
import kotlin.math.abs
import androidx.compose.ui.util.lerp
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush

class OnboardingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OnboardingScreen(onStartClick = {
                startActivity(Intent(this, SignUpActivity::class.java))
                finish()
            }, onTermsClick = {
                startActivity(Intent(this, TermsActivity::class.java))
            })
        }
    }
}

@Composable
fun OnboardingScreen(onStartClick: () -> Unit, onTermsClick: () -> Unit) {
    val pages = listOf(
        OnboardingPage(
            R.drawable.image1, "Bem-vindo ao SuperID",
            "Gerencie suas senhas de forma segura e prática. Nunca mais esqueça uma senha e mantenha suas contas organizadas."
        ),
        OnboardingPage(
            R.drawable.image2, "Login sem Senha",
            "Utilize QR Code para acessar suas contas de forma rápida e segura. Chega de digitar senhas complicadas!"
        ),
        OnboardingPage(
            R.drawable.image3, "Proteção Completa",
            "Seus dados são criptografados e protegidos contra acessos indevidos. Tecnologia de ponta para a sua segurança."
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()
    var termsAccepted by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF292E49), Color(0xFF536976), Color(0xFFBBD2C5))
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) { pageIndex ->
            OnboardingPageView(pagerState, pages[pageIndex], pageIndex)
        }

        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(pages.size) { index ->
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(
                            if (pagerState.currentPage == index) Color(0xFF292E49) else Color(0xFFEAD4BE),
                            shape = MaterialTheme.shapes.small
                        )
                        .padding(4.dp)
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            if (pagerState.currentPage == pages.lastIndex) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = termsAccepted,
                        onCheckedChange = { termsAccepted = it },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF292E49))
                    )
                    Text(
                        text = "Aceito os ",
                        color = Color.White
                    )
                    Text(
                        text = "Termos de Uso",
                        color = Color(0xFF292E49),
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable { onTermsClick() }
                    )
                }
            }
            Button(
                onClick = {
                    if (pagerState.currentPage == pages.lastIndex) {
                        if (termsAccepted) onStartClick()
                    } else {
                        coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    }
                },
                enabled = pagerState.currentPage != pages.lastIndex || termsAccepted,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF292E49))
            ) {
                Text(
                    text = if (pagerState.currentPage == pages.lastIndex) "Começar" else "Próximo",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun OnboardingPageView(pagerState: PagerState, page: OnboardingPage, pageIndex: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        val pageOffset = pagerState.currentPageOffsetFraction

        Image(
            painter = painterResource(id = page.imageId),
            contentDescription = null,
            modifier = Modifier
                .size(300.dp)
                .graphicsLayer {
                    val scale = lerp(0.5f, 1.0f, 1 - abs(pageOffset))
                    scaleX = scale
                    scaleY = scale
                }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = page.title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFEAD4BE)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = page.description,
            fontSize = 18.sp,
            color = Color(0xFFEDE0D4),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

/**
 * Classe que representa uma página do onboarding.
 */
data class OnboardingPage(val imageId: Int, val title: String, val description: String)