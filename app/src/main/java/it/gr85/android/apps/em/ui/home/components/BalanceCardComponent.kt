package it.gr85.android.apps.em.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.gr85.android.apps.em.ui.AppColors
import it.gr85.android.apps.em.ui.AppShapes
import it.gr85.android.apps.em.ui.AppSpacing


@Composable
fun BalanceCard(
    balance: Long,
    totalIncome: Long,
    totalExpense: Long,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = AppShapes.Small,
        colors = CardDefaults.cardColors(
            containerColor = AppColors.Surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
        ) {
            Text(
                "Bilancio",
                style = MaterialTheme.typography.labelMedium,
                color = AppColors.TextSecondary
            )

            Text(
                "€${balance / 100.0}",
                style = MaterialTheme.typography.headlineLarge,
                color = when {
                    balance > 0 -> AppColors.Success
                    balance < 0 -> AppColors.Error
                    else -> AppColors.TextPrimary
                }
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = AppSpacing.md),
                thickness = DividerDefaults.Thickness,
                color = AppColors.SurfaceVariant
            )

            // Breakdown (incasso/spesa)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                    Text(
                        "Entrate",
                        style = MaterialTheme.typography.labelMedium,
                        color = AppColors.TextSecondary
                    )
                    Text(
                        "+€${totalIncome / 100.0}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = AppColors.Success
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        "Uscite",
                        style = MaterialTheme.typography.labelMedium,
                        color = AppColors.TextSecondary
                    )
                    Text(
                        "-€${totalExpense / 100.0}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = AppColors.Error
                    )
                }
            }
        }
    }
}

/*
  |--------------------------|
  | B I L A N C I O          |
  |                          |
  |               XXXX.XX €  |
  |--------------------------|
  | Entrate     | Uscite     |
  | +XXXX.XX €  | -XXXX.XX € |
  |--------------------------|
 */
@Composable
fun BalanceSummaryCard(
    balance : Long,
    totalIncome : Long,
    totalExpense: Long
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = AppShapes.Small,
        colors = CardDefaults.cardColors(
            containerColor = AppColors.Surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(AppSpacing.sm),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
        ) {

            Text(
                text = "Bilancio: ",
                style = MaterialTheme.typography.labelMedium,
                color = AppColors.TextSecondary
            )
            Text(
                text = "€${balance / 100.0}",
                style = MaterialTheme.typography.headlineLarge,
                color = when {
                    balance > 0 -> AppColors.Success
                    balance < 0 -> AppColors.Error
                    else -> AppColors.TextPrimary
                }
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = AppSpacing.xxxs ),
                thickness = DividerDefaults.Thickness,
                color = AppColors.SurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
                ) {
                    Text(
                        "Entrate",
                        style = MaterialTheme.typography.labelMedium,
                        color = AppColors.TextSecondary
                    )
                    Text(
                        "+€${totalIncome / 100.0}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = AppColors.Success)
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
                ) {
                    Text(
                        "Uscite",
                        style = MaterialTheme.typography.labelMedium,
                        color = AppColors.TextSecondary
                    )
                    Text(
                        "-€${totalExpense / 100.0}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = AppColors.Error)
                }
            }
        }
    }
}

@Composable
@Preview
fun BalanceSummaryCardPreview() {
    //BalanceCard(
    BalanceSummaryCard(
        balance = 123456,
        totalIncome = 200000,
        totalExpense = 76544
    )
}