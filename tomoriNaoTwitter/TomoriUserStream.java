package TomoriNaoTwitter;

import twitter4j.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class TomoriUserStream extends UserStreamAdapter {
    private Twitter twitter;
    private String[] tomoriPictureURL;
    private int counter;

    public TomoriUserStream(){
        this.twitter = new TwitterFactory().getInstance();

        String filename = "pictureURL.txt";
        ArrayList<String> tmpArray = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String tmp;
            while ((tmp = br.readLine()) != null){
                tmpArray.add(tmp);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.tomoriPictureURL = tmpArray.toArray(new String[0]);
        this.counter = 0;

    }

    //ツイート取得時
    @Override
    public void onStatus(Status status) {
        String tweet = status.getText();
        System.out.println(status.getUser().getName() + " : " + tweet);
        //RTを除外
        if(!tweet.matches("RT @.*")) {
            if (tweet.matches(".*にゃーん.*")) {
                try {
                    //画像を順番に投げる
                    this.twitter.updateStatus(new StatusUpdate("@" + status.getUser().getScreenName()
                            + " " + tomoriPictureURL[counter % tomoriPictureURL.length]).inReplyToStatusId(status.getId()));
                    counter++;
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
            } else if (tweet.matches(".*鯖プロ.*")) {
                try {
                    this.twitter.updateStatus(new StatusUpdate("@" + status.getUser().getScreenName() + " 進捗どうですか？").inReplyToStatusId(status.getId()));
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //フォローされた時
    @Override
    public void onFollow(User source, User followedUser) {
        //フォローされた人
        System.out.println("source: " + source.getName());
        //フォローした人
        System.out.println("followedUser: " + followedUser.getName());

        if(!source.getScreenName().contains("TomoriNaoServer")) {
            try {
                this.twitter.createFriendship(source.getId());
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        TomoriUserStream tus = new TomoriUserStream();
        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.addListener(tus);
        twitterStream.user();
    }
}
