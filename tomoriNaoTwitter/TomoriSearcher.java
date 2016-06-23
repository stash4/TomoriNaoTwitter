package tomoriNaoTwitter;

import twitter4j.*;
import twitter4j.auth.AccessToken;

import java.util.*;

public class TomoriSearcher {
    private Twitter twitter;
    private Map<User, User[]> tomoriMap = new HashMap<>();

    public TomoriSearcher() {
        String consumerKey = "";
        String consumerSecret = "";
        String accessToken = "";
        String accessSecret = "";
        this.twitter = TwitterFactory.getSingleton();
        this.twitter.setOAuthConsumer(consumerKey, consumerSecret);
        this.twitter.setOAuthAccessToken(new AccessToken(accessToken, accessSecret));
    }

    //フォローしている人から名前が友利奈緒の人を取得
    public User[] getFollowTomori(String userName) throws TwitterException {
        IDs follow = twitter.getFriendsIDs(userName, -1);
        long[] usersId = follow.getIDs();
        return getTomori(usersId).toArray(new User[0]);
    }

    //フォロワーから名前が友利奈緒の人を取得
    public User[] getFollowerTomori(String userName) throws TwitterException {
        IDs follower = twitter.getFollowersIDs(userName, -1);
        long[] usersId = follower.getIDs();
        return getTomori(usersId).toArray(new User[0]);
    }

    //ユーザ名が友利奈緒の人を取得
    private ArrayList<User> getTomori(long[] userID) throws TwitterException {
        ArrayList<User> targetUsers = new ArrayList<>();
        int loopMax = userID.length / 100;
        int surplusUsers = userID.length % 100;

        for (int i = 0; i < loopMax; i++) {
            //lookupUsersの仕様が100人までの情報しか取得できないため100人ずつ処理
            ResponseList<User> users = twitter.lookupUsers(Arrays.copyOfRange(userID, i * 100, (i + 1) * 100));
            for (User user : users
                    ) {
                if (user.getName().matches(".*友.*利.*奈.*緒.*")) {
                    targetUsers.add(user);
                }
            }
        }
        //100人ずつやったあとの最後の端数を処理
        //555人のユーザ名を調べる場合500人は上で処理し,55人をこちらで処理する
        if (surplusUsers != 0) {
            ResponseList<User> users = twitter.lookupUsers(Arrays.copyOfRange(userID, loopMax * 100, loopMax * 100 + surplusUsers));
            for (User user : users) {
                if (user.getName().matches(".*友.*利.*奈.*緒.*")) {
                    targetUsers.add(user);
                }
            }
        }
        return targetUsers;
    }

    //TwitterMiniIcon取得
    public String getIconURL(String userId) throws TwitterException {
        return twitter.showUser(userId).getMiniProfileImageURL();
    }

    //Tweetする
    public void tweet(String str) throws TwitterException {
        twitter.updateStatus(str);
        System.out.println(str);
    }

    public static void main(String[] args) throws TwitterException {
        int mutualFollower = 0;
        HashSet<String> icons = new HashSet<>();    //フォローとフォロワーからの重複を削除
        TomoriSearcher tnd = new TomoriSearcher();
        String targetName = "sydosy1";
//        icons.add(tnd.getIconURL(targetName));

        //フォロワー
        User[] followerUsers = tnd.getFollowerTomori(targetName);
        for (User user : followerUsers) {
            icons.add(user.getProfileImageURL());
        }

        //フォロー
        User[] followUsers = tnd.getFollowTomori(targetName);
        for (User user : followUsers) {
            if (!icons.add(user.getProfileImageURL())) {
                mutualFollower++;
            }
        }


        tnd.tweet(targetName + "の\n" +
                "フォローから友利奈緒を" + followUsers.length + "人見つけました！" +
                "\nフォロワーから友利奈緒を" + followerUsers.length + "人見つけました！" +
                "\n相互フォローの友利奈緒は" + mutualFollower + "人です！");
    }
}
