package com.dimunoz.androidsocialconn.xml;

/**
 * Created with IntelliJ IDEA.
 * InstagramUser: diego
 * Date: 15-10-13
 * Time: 02:53 PM
 * Based on tutorial in http://androidexample.com/XML_Parsing_-_Android_Example/index.php?view=article_discription&aid=69&aaid=94
 * To change this template use File | Settings | File Templates.
 */
public class XmlContact {

    private long id;

    private String email;
    private String instagram;
    private String nickname;
    private String photo;
    private String skype;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getSkype() {
        return skype;
    }

    public void setSkype(String skype) {
        this.skype = skype;
    }
}
