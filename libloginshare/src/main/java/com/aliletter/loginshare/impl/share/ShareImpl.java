package com.aliletter.loginshare.impl.share;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;

import com.aliletter.loginshare.model.WechatMessageBody;
import com.aliletter.loginshare.model.WeiboMessageBody;
import com.aliletter.loginshare.util.Util;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.MultiImageObject;
import com.sina.weibo.sdk.api.StoryMessage;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.VideoSourceObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMusicObject;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXVideoObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;

import java.io.File;

import static com.aliletter.loginshare.util.Util.THUMB_SIZE;
import static com.aliletter.loginshare.util.Util.buildTransaction;


/**
 * Author：alilettter
 * Github: http://github.com/aliletter
 * Email: 4884280@qq.com
 * data: 2017/12/12
 */

public abstract class ShareImpl extends Share {

    public ShareImpl(AppCompatActivity activity) {
        super(activity);
    }

    @Override
    protected WeiboMultiMessage buildWeiboMultiMessage(WeiboMessageBody weiboMessageBody) {
        WeiboMultiMessage msg = new WeiboMultiMessage();
        if (weiboMessageBody.getText() != null)
            msg.textObject = getTextObject(weiboMessageBody);
        switch (weiboMessageBody.msgType) {
            case WeiboMessageBody.MSG_WEB:
                msg.mediaObject = getMediaObject(weiboMessageBody);

            case WeiboMessageBody.MSG_TEXT_IAMGE:
                if (weiboMessageBody.getImage() != null)
                    msg.imageObject = getImageObject(weiboMessageBody);
                if (weiboMessageBody.getImagesPath() != null)
                    msg.multiImageObject = getMultiImageObject(weiboMessageBody);
                break;

            case WeiboMessageBody.MSG_VIDEO:
                if (weiboMessageBody.getVideoPath() != null)
                    msg.videoSourceObject = getVideoObject(weiboMessageBody);
                break;
        }
        return msg;
    }

    protected abstract WebpageObject getMediaObject(WeiboMessageBody weiboMessageBody);

    protected abstract VideoSourceObject getVideoObject(WeiboMessageBody weiboMessageBody);

    protected abstract MultiImageObject getMultiImageObject(WeiboMessageBody weiboMessageBody);

    protected abstract ImageObject getImageObject(WeiboMessageBody weiboMessageBody);

    protected abstract TextObject getTextObject(WeiboMessageBody weiboMessageBody);

    @Override
    protected StoryMessage buildStoryMessage(WeiboMessageBody weiboMessageBody) {
        StoryMessage storyMessage = new StoryMessage();
        switch (weiboMessageBody.msgType) {
            case WeiboMessageBody.MSG_TEXT_IAMGE:
                if (weiboMessageBody.getImage() != null)
                    storyMessage.setImageUri(Uri.fromFile(new File(weiboMessageBody.getImage())));
                break;
            case WeiboMessageBody.MSG_VIDEO:
                if (weiboMessageBody.getVideoPath() != null)
                    storyMessage.setVideoUri(Uri.fromFile(new File(weiboMessageBody.getVideoPath())));
                break;
        }
        return storyMessage;
    }

    @Override
    protected BaseReq buildWebWeChatMessageBody(WechatMessageBody wechatMessageBody) {
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = wechatMessageBody.getWebpageUrl();
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = wechatMessageBody.getTitle();
        msg.description = wechatMessageBody.getDescription();
        Bitmap bmp = BitmapFactory.decodeFile(wechatMessageBody.getImage());
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
        bmp.recycle();
        msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = getWecahtTargetScene(wechatMessageBody);
        return req;
    }

    @Override
    protected BaseReq buildVideoWeChatMessageBody(WechatMessageBody wechatMessageBody) {
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        WXVideoObject video = new WXVideoObject();
        video.videoUrl = wechatMessageBody.getVideoUrl();
        WXMediaMessage msg = new WXMediaMessage(video);
        msg.title = wechatMessageBody.getTitle();
        msg.description = wechatMessageBody.getDescription();
        Bitmap bmp = BitmapFactory.decodeFile(wechatMessageBody.getImage());
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
        bmp.recycle();
        msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
        req.transaction = buildTransaction("video");
        req.message = msg;
        req.scene = getWecahtTargetScene(wechatMessageBody);
        return req;
    }

    @Override
    protected BaseReq buildMusicWeChatMessageBody(WechatMessageBody wechatMessageBody) {
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        WXMusicObject music = new WXMusicObject();
        music.musicUrl = wechatMessageBody.getMusicUrl();
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = music;
        msg.title = wechatMessageBody.getTitle();
        msg.description = wechatMessageBody.getDescription();
        Bitmap bmp = BitmapFactory.decodeFile(wechatMessageBody.getImage());
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
        bmp.recycle();
        msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
        req.transaction = buildTransaction("music");
        req.message = msg;
        req.scene = getWecahtTargetScene(wechatMessageBody);
        return req;
    }

    @Override
    protected BaseReq buildImageWeChatMessageBody(WechatMessageBody wechatMessageBody) {
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        Bitmap bmp = BitmapFactory.decodeFile(wechatMessageBody.getImage());
        WXImageObject imgObj = new WXImageObject(bmp);
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
        bmp.recycle();
        msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
        req.transaction = buildTransaction("img");
        req.message = msg;
        req.scene = getWecahtTargetScene(wechatMessageBody);
        return req;
    }


    @Override
    protected BaseReq buildTextWeChatMessageBody(WechatMessageBody wechatMessageBody) {
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        WXTextObject textObj = new WXTextObject();
        textObj.text = wechatMessageBody.getText();
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        msg.description = wechatMessageBody.getDescription();
        req.transaction = buildTransaction("text");
        req.message = msg;
        req.scene = getWecahtTargetScene(wechatMessageBody);
        return req;
    }

    protected abstract int getWecahtTargetScene(WechatMessageBody wechatMessageBody);
}
