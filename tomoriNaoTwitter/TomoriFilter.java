package TomoriNaoTwitter;


import twitter4j.*;

import java.util.ArrayList;

public class TomoriFilter extends StatusAdapter {
    private Twitter twitter;
    private ArrayList<String> addURLs;

    public TomoriFilter(){
        this.twitter = new TwitterFactory().getInstance();
        this.addURLs = new ArrayList<>();
    }

    @Override
    public void onStatus(Status status){
        System.out.println(status.getUser().getName() + " : " + status.getText());
        try {
            //ファボを飛ばす
            this.twitter.createFavorite(status.getId());

            //TweetにURLがあれば取り出す
            URLEntity[] urlEntities = status.getURLEntities();
            if( urlEntities != null && urlEntities.length > 0 ){
                for(URLEntity urlEntity: urlEntities){
                    String url = urlEntity.getExpandedURL();
                    //ToDo 外部ファイルに保存できるようにする
                    this.addURLs.add(url);
                }
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        TomoriFilter tf = new TomoriFilter();
        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        //ListenerにTwitterStream(取得可能な全Tweet)を設定
        twitterStream.addListener(tf);

        //検索する単語を指定
        String[] filterWords = {"#友利奈緒は癒やし", "#友利奈緒は癒し"};
        FilterQuery filter = new FilterQuery();
        filter.track( filterWords );
        twitterStream.filter( filter );
    }

}
