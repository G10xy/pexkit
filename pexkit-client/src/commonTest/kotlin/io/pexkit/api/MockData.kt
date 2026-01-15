package io.pexkit.api

import io.pexkit.api.model.Collection
import io.pexkit.api.model.Photo
import io.pexkit.api.model.PhotoSource
import io.pexkit.api.model.User
import io.pexkit.api.model.Video
import io.pexkit.api.model.VideoFile
import io.pexkit.api.model.VideoPicture


internal object MockData {

    val photoSource = PhotoSource(
        original = "https://images.pexels.com/photos/2014422/pexels-photo-2014422.jpeg",
        large2x = "https://images.pexels.com/photos/2014422/pexels-photo-2014422.jpeg?dpr=2&h=650&w=940",
        large = "https://images.pexels.com/photos/2014422/pexels-photo-2014422.jpeg?h=650&w=940",
        medium = "https://images.pexels.com/photos/2014422/pexels-photo-2014422.jpeg?h=350",
        small = "https://images.pexels.com/photos/2014422/pexels-photo-2014422.jpeg?h=130",
        portrait = "https://images.pexels.com/photos/2014422/pexels-photo-2014422.jpeg?h=1200&w=800",
        landscape = "https://images.pexels.com/photos/2014422/pexels-photo-2014422.jpeg?h=627&w=1200",
        tiny = "https://images.pexels.com/photos/2014422/pexels-photo-2014422.jpeg?h=200&w=280",
    )

    val photo = Photo(
        id = 2014422L,
        width = 3024,
        height = 4032,
        url = "https://www.pexels.com/photo/2014422/",
        photographer = "Joey Doe",
        photographerUrl = "https://www.pexels.com/@joey",
        photographerId = 123456L,
        avgColor = "#978E82",
        src = photoSource,
        alt = "Brown rocks during golden hour",
        liked = false,
    )

    val photoPage2 = Photo(
        id = 2014423L,
        width = 1920,
        height = 1080,
        url = "https://www.pexels.com/photo/2014423/",
        photographer = "John Smith",
        photographerUrl = "https://www.pexels.com/@john",
        photographerId = 789012L,
        avgColor = "#445566",
        src = photoSource.copy(
            original = "https://images.pexels.com/photos/2014423/pexels-photo-2014423.jpeg",
        ),
        alt = "Mountain landscape",
        liked = true,
    )

    val user = User(
        id = 123456L,
        name = "Jane Doe",
        url = "https://www.pexels.com/@jane",
    )

    val videoFileHd = VideoFile(
        id = 123456L,
        quality = "hd",
        fileType = "video/mp4",
        width = 1920,
        height = 1080,
        fps = 25.0,
        link = "https://player.vimeo.com/external/857251.hd.mp4",
    )

    val videoFileSd = VideoFile(
        id = 123457L,
        quality = "sd",
        fileType = "video/mp4",
        width = 960,
        height = 540,
        fps = 25.0,
        link = "https://player.vimeo.com/external/857251.sd.mp4",
    )

    val videoPicture = VideoPicture(
        id = 123456L,
        picture = "https://images.pexels.com/videos/857251/pictures/preview-0.jpg",
        nr = 0,
    )

    val video = Video(
        id = 857251L,
        width = 1920,
        height = 1080,
        url = "https://www.pexels.com/video/857251/",
        image = "https://images.pexels.com/videos/857251/free-video-857251.jpg",
        fullRes = null,
        tags = emptyList(),
        duration = 35,
        user = user,
        videoFiles = listOf(videoFileHd, videoFileSd),
        videoPictures = listOf(videoPicture),
    )

    val collection = Collection(
        id = "abc123",
        title = "Nature",
        description = "Beautiful nature photos",
        private = false,
        mediaCount = 250,
        photosCount = 200,
        videosCount = 50,
    )
}
