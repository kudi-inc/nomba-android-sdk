package com.nomba.wraith.core

import android.view.View
import androidx.viewbinding.ViewBinding


///The Shelter Architecture is a lightweight model I created and use
///to easily manage the handling of UI elements on screens without
///having to resort to fragments or activities, hereby preserving context
open class Shelter {

    var layout_data_binding : ViewBinding

    constructor(layout: ViewBinding){
        this.layout_data_binding = layout
    }

    open fun showShelter(){
        layout().root.requestFocus()
        layout_data_binding.root.visibility = View.VISIBLE
    }

    open fun hideShelter(){
        //layout().root.clearFocus()
        layout_data_binding.root.visibility = View.GONE
    }

    open fun layout(): ViewBinding{
        return layout_data_binding
    }

}