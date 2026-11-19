package bo.edu.ucb.segura.core.ui

import android.widget.ImageView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
actual fun UcbLogo(modifier: Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            ImageView(context).apply {
                val resId = context.resources.getIdentifier("logo_ucb", "drawable", context.packageName)
                if (resId != 0) {
                    setImageResource(resId)
                } else {
                    setImageResource(android.R.drawable.ic_menu_compass)
                }
                scaleType = ImageView.ScaleType.FIT_CENTER
            }
        }
    )
}
