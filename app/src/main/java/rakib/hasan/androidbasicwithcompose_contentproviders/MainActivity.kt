package rakib.hasan.androidbasicwithcompose_contentproviders

import android.Manifest
import android.content.ContentUris
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import coil.compose.AsyncImage
import rakib.hasan.androidbasicwithcompose_contentproviders.ui.theme.AndroidBasicWithCompose_ContentProvidersTheme
import java.util.Calendar

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<ImageViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //assume user use the latest android device
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES
            ),
            0
        )

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME
        )
        val millisYesterday = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -100)
        }
        val selection = "${MediaStore.Images.Media.DATE_TAKEN} >= ?"
        val selectionArgs = arrayOf(
            millisYesterday.toString()
        )
        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"
        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
            val images = mutableListOf<Image>()
            while (cursor.moveToNext()){
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(idColumn)
                val uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                images.add(Image(id, name, uri))
            }
            viewModel.updateImages(images)
        }

        setContent {
            AndroidBasicWithCompose_ContentProvidersTheme {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ){
                    items(viewModel.images) { image ->
                        AsyncImage(
                            model = image.uri,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Image title : ${image.name}")
                    }
                }
            }
        }
    }
}

data class Image(
    val id: Long,
    val name: String,
    val uri: Uri
)