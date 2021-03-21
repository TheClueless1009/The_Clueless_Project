# Twitter crawler libs
import tweepy
import datetime
import numpy as np

# Firestore lib
import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore

# Reddit crawler libs
import praw

# Wordcloud, Sentiment Analysis
import os
from os import path
from wordcloud import WordCloud # new
from textblob import TextBlob # new
import re # new
import base64 # new
import matplotlib.pyplot as plt # new

# File issue with Android - uncomment when running on Android
from os.path import dirname, join

filename1 = join(dirname(__file__), "theclueless-e4926-firebase-adminsdk-9h05x-bbd334aabe.json")
filename2 = join(dirname(__file__), "wc.png")
filename3 = join(dirname(__file__), "wc.txt")

# Parent crawler
class Crawler(): 
    def crawl(self): 
        pass

    def exportToDB(self): 
        pass

    def generateSentimentAnalysis(self, fs_db, tweets, submissions):
        all_posts = []
        
        for sub in submissions:
            all_posts.append(sub)

        for tweet in tweets:
            all_posts.append(tweet)
        
        count = 0
        for c in all_posts:
            blob = TextBlob(c)

            polarity = blob.sentiment.polarity
            subjectivity = blob.sentiment.subjectivity

            doc_ref = fs_db.collection(u'sentimentAnalysis').document('first')
            if (polarity != 0 and subjectivity != 0):
                count += 1
                doc_ref.set({str(count): {'post': c, 'polarity': polarity, 'subjectivity':subjectivity}}, merge=True)

        with open(filename3, 'w') as output:
            for data in all_posts:
                output.write('%s\n' % data)

    def generateWordCloud(self):
        d = path.dirname(__file__) if '__file__' in locals() else os.getcwd()
        
        text = open(path.join(d, filename3)).read()
        
        wordcloud = WordCloud().generate(text)
        
        plt.imshow(wordcloud, interpolation='bilinear')
        plt.axis('off')
        
        wordcloud = WordCloud(max_font_size=40).generate(text)
        plt.figure()
        plt.imshow(wordcloud, interpolation='bilinear')
        plt.axis('off')
        image = wordcloud.to_file(filename2)

        with open(filename2, 'rb') as img_file:
            b64_string = base64.b64encode(img_file.read())

        wordcloud_img = str(b64_string.decode('utf-8'))

        return wordcloud_img

    def clean(self, sub):
        sub = re.sub(r'^RT[\s]+', '', sub)
        sub = re.sub(r'https?:\/\/.*[\r\n]*', '', sub)
        sub = re.sub(r'#', '', sub)
        sub = re.sub(r'@[A-Za-z0–9]+', '', sub) 

        return sub
  
class TwitterCrawler(Crawler, object): 
    def __init__(self, fs_db, keyword):
        self.fs_db = fs_db
        self.keyword = keyword
        self.api = self.initTweepy()

    def initTweepy(self):
        CONSUMER_KEY     = 'V1lNblYRlu57pqA9NCoxOOC4B'
        CONSUMER_SECRET  = '0Q5MBXcHi0eiycmu0Q7lLGzfMHuCw9Y8IwZaxV3dOFYH5DVsCC'
        ACCESS_KEY       = '1370328661618880515-29x3QQ6aGziflRMYLtrEu4ZL7mLbov'
        ACCESS_SECRET    = 'dejwFK9J9pH2VHS7WYN2FO3YJ1WhpbfskACE3H48HgbwP'

        auth = tweepy.OAuthHandler(CONSUMER_KEY, CONSUMER_SECRET)
        auth.set_access_token(ACCESS_KEY, ACCESS_SECRET)
        api = tweepy.API(auth)

        return api
        
    def crawl(self): 
        print(self.fs_db)
        retrievedTweets = []
        wc_tweets = []

        count = 1
        
        today = datetime.datetime.now()
        today = today.replace(hour=23, minute=59, second=59, microsecond=999999)
        gap = 1
        yesterday = today - datetime.timedelta(gap) 
        nextDay = yesterday + datetime.timedelta(gap)
        
        while True:
            try:
                lst = tweepy.Cursor(self.api.search, lang='en',result_type="popular", q=self.keyword, count=10, until=nextDay.date()).items(10)
                for tweet in lst:
                    self.data = [tweet.created_at, tweet.id, tweet.text,
                    tweet.user._json['screen_name'], tweet.user._json['name'], 
                    tweet.favorite_count, tweet.retweet_count, tweet.user.location]
                    self.data = tuple(self.data)
                    retrievedTweets.append(self.data)
                    wc_tweets.append(self.clean(tweet.text))
                break
            except tweepy.TweepError as e:
                print(e.reason)
                continue
            except StopIteration: 
                break

        self.exportToDB(retrievedTweets)

        return wc_tweets

    def exportToDB(self, tweets):
        print('hello')
        print(tweets)
        for t in range(len(tweets)):
            for x in range(len(tweets[t])):
                print(self.fs_db)
                doc_ref = self.fs_db.collection(u'twitter').document(str(tweets[t][1]))
                doc_ref.set({
                    u'created_date': str(tweets[t][0]),
                    u'id': str(tweets[t][1]),
                    u'tweet': tweets[t][2],
                    u'screen_name': tweets[t][3],
                    u'name': tweets[t][4],
                    u'likes': tweets[t][5],
                    u'retweets': tweets[t][6],
                    u'location': tweets[t][7]
                })
  
class RedditCrawler(Crawler, object): 
    def __init__(self, fs_db, keyword):
        self.fs_db = fs_db
        self.keyword = keyword
        
    def crawl(self):
        subs = [] 
        reddit = praw.Reddit(
                    client_id='QRl_4bwjckcg9A',
                    client_secret='dsavqFoOk5NgWEOWtMf9NknwxRIoIw',
                    password='P@ssword123',
                    user_agent='cluelessv1',
                    username='theclueless1009'
                )
        submissions = reddit.subreddit('all').search(self.keyword, sort='hot', limit=10, time_filter='week')

        self.exportToDB(submissions)

        for sub in submissions:
            subs.append(self.clean(sub.title))

        return subs

    def exportToDB(self, submissions):
        print('hello')
        print(self.fs_db)
        for sub in submissions:
            doc_ref = self.fs_db.collection(u'reddit').document(sub.id)
            doc_ref.set({
                u'content': sub.selftext,
                u'upvote_ratio': sub.upvote_ratio,
                u'score': sub.score,
                u'title': sub.title,
                u'id': sub.id,
                u'total_awards_received': sub.total_awards_received,
                u'created_utc': sub.created_utc
            })

def initFirestore():
    cred = credentials.Certificate(filename1)
    firebase_admin.initialize_app(cred)
    db = firestore.client()
    return db

def main(keyword):
    fs_db = initFirestore()
    # keyword = "myanmar"

    twitterCrawler = TwitterCrawler(fs_db,keyword) 
    cleaned_tweets = twitterCrawler.crawl() 

    redditCrawler = RedditCrawler(fs_db, keyword) 
    cleaned_submissions = redditCrawler.crawl() 

    crawler = Crawler()
    crawler.generateSentimentAnalysis(fs_db, cleaned_tweets, cleaned_submissions)
    wordcloud_img = crawler.generateWordCloud()

    # Send wordcloud to DB
    doc_ref = fs_db.collection(u'wordcloud').document('first')
    doc_ref.set({
        u'image': wordcloud_img
    })