package com.myapp.greetingcard

import android.R.attr.contentDescription
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import com.myapp.greetingcard.R

@Composable

fun AddCardScreen(navigator: NavHostController,
                  changeMessage: (String) -> Unit) {
//
//    var enWord = ""
//
//    var vnWord = ""

//    var enWord by remember { mutableStateOf("") }
//    //rotate and the words i type gone
//    var vnWord by remember { mutableStateOf("") }

    var enWord by rememberSaveable { mutableStateOf("") }

    var vnWord by rememberSaveable { mutableStateOf("") }

    changeMessage("please add card")
    Column() {

        TextField(

            value = enWord,

            onValueChange = { enWord = it },

            modifier = Modifier.semantics{contentDescription = "English String"},

            label = { Text(stringResource(id = R.string.English_label))
            }


        )

        TextField(

            value = vnWord,

            onValueChange = { vnWord = it },

            label = { Text(stringResource(id = R.string.Vietnamese_label)) }

        )

        Button(onClick = {

            Log.d(

                "TEST", "Adding a card with words: "

                        + enWord + " and " + vnWord

            )


        }

        ) {

            Text("Add")

        }

    }

}