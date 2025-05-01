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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.puc.superid.R
import com.puc.superid.ui.registration.SignUpActivity
import com.puc.superid.ui.terms.TermsActivity
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import androidx.compose.ui.util.lerp

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

data class OnboardingPage(val image: Int, val title: String, val description: String)

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

    val solwayFamily = FontFamily(
        Font(R.font.solway_bold, FontWeight.Bold),
        Font(R.font.solway_regular, FontWeight.Normal),
        Font(R.font.solway_medium, FontWeight.Medium),
        Font(R.font.solway_light, FontWeight.Light),
        Font(R.font.solway_extrabold, FontWeight.ExtraBold)
    )

    Box(modifier = Modifier.fillMaxSize()) {

        val currentPage = pagerState.currentPage
        val currentOffset = pagerState.currentPageOffsetFraction
        val alpha = lerp(0.5f, 1f, 1f - currentOffset.absoluteValue.coerceIn(0f, 1f))

        Image(
            painter = painterResource(id = pages[currentPage].image),
            contentDescription = null,
            contentScale = ContentScale.FillHeight,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { this.alpha = alpha }
                .matchParentSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xA3000000),
                            Color(0xB5000000),
                            Color(0x7A000000)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) { index ->
                OnboardingPageView(
                    page = pages[index],
                    fontFamily = solwayFamily
                )
            }

            Row(
                modifier = Modifier.padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pages.size) { index ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (pagerState.currentPage == index) 12.dp else 8.dp)
                            .background(
                                color = if (pagerState.currentPage == index) Color.White else Color.LightGray,
                                shape = RoundedCornerShape(50)
                            )
                    )
                }
            }

            if (pagerState.currentPage == pages.lastIndex) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Checkbox(
                        checked = termsAccepted,
                        onCheckedChange = { termsAccepted = it },
                        colors = CheckboxDefaults.colors(
                            uncheckedColor = Color.White,
                            checkedColor = Color.Black
                        ),
                    )
                    Text(text = "Aceito os ", color = Color.White, fontSize = 14.sp)
                    Text(
                        text = "Termos de Uso",
                        color = Color.Cyan,
                        textDecoration = TextDecoration.Underline,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { onTermsClick() }
                    )
                }
            }

            val isLastPage = pagerState.currentPage == pages.lastIndex
            val isEnabled = !isLastPage || (isLastPage && termsAccepted)

            Button(
                modifier = Modifier
                    .height(48.dp)
                    .width(150.dp),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(),
                onClick = {
                    if (isLastPage) {
                        if (termsAccepted) onStartClick()
                    } else {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                enabled = isEnabled,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = if (!isEnabled)
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFF464647),
                                        Color(0xFF323232)
                                    )
                                )
                            else
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFF021A4C),
                                        Color(0xFF045DDD)
                                    )
                                ),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isLastPage) "Começar" else "Próximo",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun OnboardingPageView(page: OnboardingPage, fontFamily: FontFamily) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = page.title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = fontFamily,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = page.description,
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = fontFamily,
            color = Color(0xFFE0E0E0),
            lineHeight = 22.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}