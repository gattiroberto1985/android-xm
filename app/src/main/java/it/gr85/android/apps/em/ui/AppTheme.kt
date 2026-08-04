package it.gr85.android.apps.em.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = AppColors.Primary,
            secondary = AppColors.PrimaryLight,
            tertiary = AppColors.PrimaryDark,
            background = AppColors.Background,
            surface = AppColors.Surface,
            onBackground = AppColors.TextPrimary,
            onSurface = AppColors.TextPrimary
        ),
        typography = Typography(
            headlineLarge = AppTypography.HeadlineLarge,
            headlineMedium = AppTypography.HeadlineMedium,
            titleLarge = AppTypography.TitleLarge,
            bodyLarge = AppTypography.BodyLarge,
            bodyMedium = AppTypography.BodyMedium,
            labelMedium = AppTypography.LabelMedium
        )
    ) {
        content()
    }
}