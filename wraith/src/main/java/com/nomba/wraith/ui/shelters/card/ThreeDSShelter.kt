package com.nomba.wraith.ui.shelters.card

import com.nomba.wraith.core.NombaManager
import com.nomba.wraith.core.Shelter
import com.nomba.wraith.databinding.ThreedsViewBinding

class ThreeDSShelter(var manager: NombaManager, activityThreedsViewBinding: ThreedsViewBinding) : Shelter(activityThreedsViewBinding) {

    override fun layout(): ThreedsViewBinding {
        return super.layout() as ThreedsViewBinding
    }

    var urlToLoad : String = ""

    override fun showShelter() {
        super.showShelter()
        layout().webview.loadUrl(urlToLoad)
    }

}