"""Sentiment Analysis

This script crawls Twitter and Reddit data using API wrappers, 'tweepy' and 'praw' respectively.

This script requires the 'textblob' module for the crawled data's sentiment analysis.

This script uses the 're' module for regular expressions to replace texts to blanks for sanitisation.

The 'matplotlib.pyplot' and 'wordcloud' modules are used for the visualisation of crawled data in the form of a word cloud.

The 'firebase_admin' module is used to connect and post data into an instance of Firestore database.

The 'base64' module is used to encode generated word cloud images into base64 format.

This file can also be imported as a module and contains the following functions:

    * initFirestore - Creates instance of Firebase Firestore DB and returns it to main function to be used by crawlers
    * crawl - Returns crawled data from child crawlers to be used to generate sentiment analysis and word cloud
    * generateSentimentAnalysis - Generates polarity and subjectivity values from posts passed into it and posts the values onto Firestore DB
    * generateWordCloud - Reads from text file and generates word cloud image from text file filled with combined statements and encodes the image into base64 format
    * initTweepy - Initializes Tweepy using provided Twitter credentials
    * exportToDB - Exports crawled Twitter and Reddit posts and their metadata onto Firestore DB
    * main - Calls initFirestore and executes individual functions to start crawling of data. Retrieves keyword input by Android user using chaquopy
"""
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
# from os.path import dirname, join

# filename1 = join(dirname(file), "theclueless-e4926-firebase-adminsdk-9h05x-bbd334aabe.json")
# filename2 = join(dirname(file), "wc.png")
# filename3 = join(dirname(file), "wc.txt")

# Parent crawler
class Crawler(): 
    """
    A class used to manage and analyse crawled data using child crawlers


    Attributes
    ----------
    NIL

    Methods
    -------
    crawl()
        Used by child crawler classes for crawling data from their specific social media platforms, but declared here in Parent Crawler
    exportToDB()
        Used by child crawler classes to export crawled data onto Firestore DB, but declared here in Parent Crawler
    generateSentimentAnalysis(fs_db, cleaned_submissions, cleaned_tweets)
        Generates polarity and subjectivity values of crawled data and posts them onto Firestore DB
    generateWordCloud()
        Reads from text file and generates word cloud representation of crawled data
    """
    def crawl(self): 
        pass

    def exportToDB(self): 
        pass

    def generateSentimentAnalysis(self, fs_db, cleaned_submissions, cleaned_tweets):
        """Generates polarity and subjectivity values of crawled data and posts them onto Firestore DB

        Parameters
        ----------
        fs_db: Firestore Client
        cleaned_submissions: list
        cleaned_tweets: list

        Raises
        ------
        If all_posts list is empty, raise exception

        Returns
        -------
        NIL
        """
        all_posts = []

        for p in range(len(cleaned_submissions)):
            print('reddit', self.clean(cleaned_submissions[p][3]))
            all_posts.append(self.clean(cleaned_submissions[p][3]))

        for t in range(len(cleaned_tweets)):
            print('twitter', self.clean(cleaned_tweets[t][2]))
            all_posts.append(self.clean(cleaned_tweets[t][2]))
        
        if len(all_posts) == 0:
            raise Exception("No crawled data")

        count = 0

        for c in all_posts:
            blob = TextBlob(c)

            polarity = blob.sentiment.polarity
            subjectivity = blob.sentiment.subjectivity

            doc_ref = fs_db.collection(u'sentimentAnalysis').document('first')
            if (polarity != 0 and subjectivity != 0):
                count += 1
                doc_ref.set({str(count): {'post': c, 'polarity': polarity, 'subjectivity':subjectivity}}, merge=True)

        with open('wc.txt', 'w') as output:
            for data in all_posts:
                output.write('%s\n' % data)

    def generateWordCloud(self):
        """Reads from text file and generates word cloud image. Image is encoded to base64 format

        Parameters
        ----------
        NIL

        Raises
        ------
        If base64 string is empty, raise exception

        Returns
        -------
        Returns word cloud base64 string
        """
        d = path.dirname(__file__) if '__file__' in locals() else os.getcwd()
        
        text = open(path.join(d, 'wc.txt')).read()
        
        wordcloud = WordCloud().generate(text)
        
        plt.imshow(wordcloud, interpolation='bilinear')
        plt.axis('off')
        
        wordcloud = WordCloud(max_font_size=40).generate(text)
        plt.figure()
        plt.imshow(wordcloud, interpolation='bilinear')
        plt.axis('off')
        image = wordcloud.to_file('wc.png')

        with open('wc.png', 'rb') as img_file:
            b64_string = base64.b64encode(img_file.read())

        wordcloud_img = str(b64_string.decode('utf-8'))

        if not wordcloud_img:
            raise Exception("No base64 string encoded")

        return wordcloud_img

    def clean(self, sub):
        """Cleans text passed into it

        Parameters
        ----------
        sub: str

        Raises
        ------
        NIL

        Returns
        -------
        Returns cleaned string
        """
        sub = re.sub(r'^RT[\s]+', '', sub)
        sub = re.sub(r'https?:\/\/.*[\r\n]*', '', sub)
        sub = re.sub(r'#', '', sub)
        sub = re.sub(r'@[A-Za-z0–9]+', '', sub) 

        return sub
  
class TwitterCrawler(Crawler, object): 
    """
    A class used to crawl Twitter data


    Parameters
    ----------
    fs_db: Firestore client
    keyword: str
    api: API


    Attributes
    ----------
    NIL

    Methods
    -------
    initTweepy()
        Initializes Tweepy using provided Twitter credentials
    crawl()
        Crawls tweets using Tweepy and returns them
    exportToDB(tweets)
        Exports tweets to Firestore DB
    """
    def __init__(self, fs_db, keyword):
        self.fs_db = fs_db
        self.keyword = keyword
        self.api = self.initTweepy()

    def initTweepy(self):
        """Initializes Tweepy using Twitter credentials

        Parameters
        ----------
        NIL

        Raises
        ------
        NIL

        Returns
        -------
        Returns Tweepy API
        """
        CONSUMER_KEY     = 'V1lNblYRlu57pqA9NCoxOOC4B'
        CONSUMER_SECRET  = '0Q5MBXcHi0eiycmu0Q7lLGzfMHuCw9Y8IwZaxV3dOFYH5DVsCC'
        ACCESS_KEY       = '1370328661618880515-29x3QQ6aGziflRMYLtrEu4ZL7mLbov'
        ACCESS_SECRET    = 'dejwFK9J9pH2VHS7WYN2FO3YJ1WhpbfskACE3H48HgbwP'

        auth = tweepy.OAuthHandler(CONSUMER_KEY, CONSUMER_SECRET)
        auth.set_access_token(ACCESS_KEY, ACCESS_SECRET)
        api = tweepy.API(auth)

        return api
        
    def crawl(self): 
        """Crawls tweets and returns them

        Parameters
        ----------
        NIL

        Raises
        ------
        If Tweepy Cursor fails, raise Tweepy exceptions

        Returns
        -------
        Returns retrieved tweets
        """
        retrievedTweets = []

        count = 1
        
        today = datetime.datetime.now()
        today = today.replace(hour=23, minute=59, second=59, microsecond=999999)
        gap = 1
        yesterday = today - datetime.timedelta(gap) 
        nextDay = yesterday + datetime.timedelta(gap)
        
        while True:
            try:
                lst = tweepy.Cursor(self.api.search, lang='en', q=self.keyword, count=50, until=nextDay.date(), result_type='popular').items(50)
                for tweet in lst:
                    self.data = [tweet.created_at, tweet.id, tweet.text,
                    tweet.user._json['screen_name'], tweet.user._json['name'], 
                    tweet.favorite_count, tweet.retweet_count, tweet.user.location]
                    self.data = tuple(self.data)
                    retrievedTweets.append(self.data)
                break
            except tweepy.TweepError as e:
                print(e.reason)
                continue
            except StopIteration: 
                break

        return retrievedTweets

    def exportToDB(self, tweets):
        """Export tweets to Firestore DB

        Parameters
        ----------
        tweets: list

        Raises
        ------
        NIL

        Returns
        -------
        NIL
        """
        for t in range(len(tweets)):
            for x in range(len(tweets[t])):
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
    """
    A class used to crawl Reddit data


    Parameters
    ----------
    fs_db: Firestore client
    keyword: str


    Attributes
    ----------
    NIL

    Methods
    -------
    crawl()
        Crawls reddit submissions using PRAW and returns them
    exportToDB(submissions)
        Exports submissions to Firestore DB
    """
    def __init__(self, fs_db, keyword):
        self.fs_db = fs_db
        self.keyword = keyword
        
    def crawl(self):
        """Crawls reddit posts and returns them

        Parameters
        ----------
        NIL

        Raises
        ------
        NIL

        Returns
        -------
        Returns retrieved reddit posts
        """
        retrievedSubs = []
        reddit = praw.Reddit(
                    client_id='QRl_4bwjckcg9A',
                    client_secret='dsavqFoOk5NgWEOWtMf9NknwxRIoIw',
                    password='P@ssword123',
                    user_agent='cluelessv1',
                    username='theclueless1009'
                )
        submissions = reddit.subreddit('all').search(self.keyword, sort='relevance', limit=50, time_filter='week')

        for sub in submissions:
            self.data = [sub.selftext, sub.upvote_ratio, sub.score,
            sub.title, sub.id, sub.total_awards_received, sub.created_utc]
            self.data = tuple(self.data)
            retrievedSubs.append(self.data)

        return retrievedSubs

    def exportToDB(self, submissions):
        """Export submissions to Firestore DB

        Parameters
        ----------
        submissions: list

        Raises
        ------
        NIL

        Returns
        -------
        NIL
        """
        for p in range(len(submissions)):
            for x in range(len(submissions[p])):
                doc_ref = self.fs_db.collection(u'reddit').document(str(submissions[p][4]))
                doc_ref.set({
                    u'content': str(submissions[p][0]),
                    u'upvote_ratio': str(submissions[p][1]),
                    u'score': submissions[p][2],
                    u'title': submissions[p][3],
                    u'id': submissions[p][4],
                    u'total_awards_received': submissions[p][5],
                    u'created_utc': submissions[p][6]
                })

def initFirestore():
    """Initializes Firestore Client

    Parameters
    ----------
    NIL

    Raises
    ------
    NIL

    Returns
    -------
    Returns Firestore client 
    """
    cred = credentials.Certificate('theclueless-e4926-firebase-adminsdk-9h05x-bbd334aabe.json')
    firebase_admin.initialize_app(cred)
    db = firestore.client()
    return db

def main(keyword):
    """Calls initFirestore and executes individual functions to start crawling of data. Retrieves keyword input by Android user using chaquopy

    Parameters
    ----------
    keyword: str

    Raises
    ------
    NIL

    Returns
    -------
    NIL
    """
    fs_db = initFirestore()
    # keyword = "whatshappeninginmyanmar"
    all_posts = []

    twitterCrawler = TwitterCrawler(fs_db,keyword) 
    cleaned_tweets = twitterCrawler.crawl() 

    redditCrawler = RedditCrawler(fs_db, keyword) 
    cleaned_submissions = redditCrawler.crawl() 

    twitterCrawler.exportToDB(cleaned_tweets)
    redditCrawler.exportToDB(cleaned_submissions)

    crawler = Crawler()

    crawler.generateSentimentAnalysis(fs_db, cleaned_submissions, cleaned_tweets)
    wordcloud_img = crawler.generateWordCloud()

    # Send wordcloud to DB
    doc_ref = fs_db.collection(u'wordcloud').document('first')
    doc_ref.set({
        u'image': wordcloud_img
    })