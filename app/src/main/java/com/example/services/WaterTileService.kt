package com.example.services

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.example.App
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WaterTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        val tile = qsTile
        if (tile != null) {
            tile.state = Tile.STATE_INACTIVE
            tile.updateTile()
        }
    }

    override fun onClick() {
        super.onClick()
        val app = applicationContext as App
        
        CoroutineScope(Dispatchers.IO).launch {
            app.repository.addWater(250)
            
            withContext(Dispatchers.Main) {
                val tile = qsTile
                if (tile != null) {
                    tile.state = Tile.STATE_ACTIVE
                    tile.updateTile()
                    
                    // Revert visually after a short delay
                    kotlinx.coroutines.delay(1000)
                    tile.state = Tile.STATE_INACTIVE
                    tile.updateTile()
                }
            }
        }
    }
}
