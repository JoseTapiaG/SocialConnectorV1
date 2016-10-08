package com.dimunoz.androidsocialconn.receivemessages;

import com.dimunoz.androidsocialconn.xml.XmlContact;

import java.io.File;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * InstagramUser: diego
 * Date: 18-12-13
 * Time: 04:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class PersonalMessage {

    private File audioFile;
    private XmlContact author;
    private String content;
    private Date datetime;
    private Boolean hasAttachedAudio;
    private Boolean hasAttachedImage;
    private Boolean hasAttachedVideo;
    private File imageFile;
    private Boolean seen;
    private File videoFile;

    public File getAudioFile() {
        return audioFile;
    }

    public void setAudioFile(File audioFile) {
        this.audioFile = audioFile;
    }

    public XmlContact getAuthor() { return author; }

    public void setAuthor(XmlContact author) { this.author = author; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public Date getDatetime() { return datetime; }

    public void setDatetime(Date datetime) { this.datetime = datetime; }

    public Boolean getHasAttachedAudio() {
        return hasAttachedAudio;
    }

    public void setHasAttachedAudio(Boolean hasAttachedAudio) {
        this.hasAttachedAudio = hasAttachedAudio;
    }

    public Boolean getHasAttachedImage() {
        return hasAttachedImage;
    }

    public void setHasAttachedImage(Boolean hasAttachedImage) {
        this.hasAttachedImage = hasAttachedImage;
    }

    public Boolean getHasAttachedVideo() {
        return hasAttachedVideo;
    }

    public void setHasAttachedVideo(Boolean hasAttachedVideo) {
        this.hasAttachedVideo = hasAttachedVideo;
    }

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public File getVideoFile() {
        return videoFile;
    }

    public void setVideoFile(File videoFile) {
        this.videoFile = videoFile;
    }
}
