package com.ifcompany.musicstreaming

import com.ifcompany.musicstreaming.service.MusicDto
import com.ifcompany.musicstreaming.service.MusicEntity

fun MusicEntity.mapper(id: Long): MusicModel =
    MusicModel(
        id = id,
        streamUrl = streamUrl,
        coverUrl = coverUrl,
        track = track,
        artist = artist
    )


fun MusicDto.mapper(): PlayerModel =
    PlayerModel(
        playMusicList = musics.mapIndexed { index, musicEntity ->
            musicEntity.mapper(index.toLong()) }
    )