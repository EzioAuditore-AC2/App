package com.example.podcast

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

/**
 * Wraps Android Media3 ExoPlayer for legitimate direct-stream podcast playback
 * (e.g., official public RSS audio enclosures), exposing clean callbacks for Compose UI.
 */
class Media3PodcastPlayer(
    context: Context,
    private val onStateChanged: (
        isPlaying: Boolean,
        isBuffering: Boolean,
        positionMs: Long,
        durationMs: Long,
        errorMessage: String?
    ) -> Unit
) {
    private val appContext = context.applicationContext
    private var exoPlayer: ExoPlayer? = null
    private var loadedUrl: String? = null

    private fun getOrCreatePlayer(): ExoPlayer {
        val existing = exoPlayer
        if (existing != null) return existing

        val created = ExoPlayer.Builder(appContext).build()
        created.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                emitSnapshot(error = null)
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                emitSnapshot(error = null)
            }

            override fun onPlayerError(error: PlaybackException) {
                emitSnapshot(
                    error = "Unable to stream audio right now (${error.errorCodeName}). You can still open the episode on the provider's website."
                )
            }
        })
        exoPlayer = created
        return created
    }

    fun playDirectStream(streamUrl: String, speed: Float = 1.0f) {
        val player = getOrCreatePlayer()
        if (loadedUrl != streamUrl) {
            loadedUrl = streamUrl
            player.setMediaItem(MediaItem.fromUri(streamUrl))
            player.prepare()
        }
        player.setPlaybackSpeed(speed)
        player.play()
        emitSnapshot(error = null)
    }

    fun togglePlayPause() {
        val player = exoPlayer ?: return
        if (player.isPlaying) {
            player.pause()
        } else {
            if (player.playbackState == Player.STATE_ENDED) {
                player.seekTo(0L)
            }
            player.play()
        }
        emitSnapshot(error = null)
    }

    fun seekTo(positionMs: Long) {
        val player = exoPlayer ?: return
        val maxDuration = player.duration.takeIf { it > 0L } ?: Long.MAX_VALUE
        player.seekTo(positionMs.coerceIn(0L, maxDuration))
        emitSnapshot(error = null)
    }

    fun skipBy(deltaMs: Long) {
        val player = exoPlayer ?: return
        seekTo(player.currentPosition + deltaMs)
    }

    fun setSpeed(speed: Float) {
        exoPlayer?.setPlaybackSpeed(speed)
        emitSnapshot(error = null)
    }

    fun pollProgress() {
        val player = exoPlayer ?: return
        if (player.isPlaying || player.playbackState == Player.STATE_BUFFERING) {
            emitSnapshot(error = null)
        }
    }

    fun stop() {
        exoPlayer?.stop()
        loadedUrl = null
        emitSnapshot(error = null)
    }

    fun release() {
        exoPlayer?.release()
        exoPlayer = null
        loadedUrl = null
    }

    private fun emitSnapshot(error: String?) {
        val player = exoPlayer
        val isPlaying = player?.isPlaying == true
        val isBuffering = player?.playbackState == Player.STATE_BUFFERING
        val pos = player?.currentPosition?.coerceAtLeast(0L) ?: 0L
        val dur = player?.duration?.takeIf { it > 0L } ?: 0L
        onStateChanged(isPlaying, isBuffering, pos, dur, error)
    }
}
