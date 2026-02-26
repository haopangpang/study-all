"""
自然语言处理(NLP)基础教程
从Java开发者角度理解NLP核心概念
"""

import nltk
import jieba
from collections import Counter
import re
import numpy as np
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.naive_bayes import MultinomialNB
from sklearn.pipeline import Pipeline
from transformers import pipeline, AutoTokenizer, AutoModel

class NLPFundamentals:
    """NLP基础概念和实现"""
    
    def __init__(self):
        # 下载必要的NLTK数据
        try:
            nltk.download('punkt', quiet=True)
            nltk.download('stopwords', quiet=True)
        except:
            print("NLTK数据下载失败，请检查网络连接")
    
    def text_preprocessing(self, text):
        """文本预处理示例"""
        print("=== 文本预处理 ===")
        
        # 原始文本
        print(f"原始文本: {text}")
        
        # 1. 分词
        chinese_text = "我爱学习人工智能技术"
        chinese_tokens = list(jieba.cut(chinese_text))
        print(f"中文分词: {chinese_tokens}")
        
        english_text = "I love learning artificial intelligence"
        english_tokens = nltk.word_tokenize(english_text.lower())
        print(f"英文分词: {english_tokens}")
        
        # 2. 去除停用词
        from nltk.corpus import stopwords
        stop_words = set(stopwords.words('english'))
        filtered_tokens = [word for word in english_tokens if word not in stop_words]
        print(f"去除停用词后: {filtered_tokens}")
        
        # 3. 词干提取
        from nltk.stem import PorterStemmer
        stemmer = PorterStemmer()
        stemmed_tokens = [stemmer.stem(word) for word in filtered_tokens]
        print(f"词干提取后: {stemmed_tokens}")
        
        return chinese_tokens, english_tokens
    
    def text_representation(self, texts):
        """文本表示方法"""
        print("\n=== 文本表示方法 ===")
        
        # 1. 词袋模型(Bag of Words)
        print("1. 词袋模型:")
        vocab = set()
        for text in texts:
            tokens = jieba.cut(text)
            vocab.update(tokens)
        
        vocab_list = sorted(list(vocab))
        print(f"词汇表: {vocab_list[:10]}...")  # 显示前10个
        
        # 2. TF-IDF表示
        print("\n2. TF-IDF向量化:")
        vectorizer = TfidfVectorizer(max_features=10)
        tfidf_matrix = vectorizer.fit_transform(texts)
        print(f"TF-IDF矩阵形状: {tfidf_matrix.shape}")
        print(f"特征名称: {vectorizer.get_feature_names_out()[:5]}")
        
        return tfidf_matrix
    
    def sentiment_analysis_demo(self):
        """情感分析示例"""
        print("\n=== 情感分析演示 ===")
        
        # 准备示例数据
        train_texts = [
            "这个产品非常好用，我很满意",
            "服务质量很差，不推荐",
            "性价比很高，值得购买",
            "物流太慢了，体验不好",
            "功能强大，操作简单"
        ]
        train_labels = [1, 0, 1, 0, 1]  # 1:正面, 0:负面
        
        # 创建朴素贝叶斯分类器管道
        classifier = Pipeline([
            ('tfidf', TfidfVectorizer()),
            ('clf', MultinomialNB())
        ])
        
        # 训练模型
        classifier.fit(train_texts, train_labels)
        
        # 测试预测
        test_texts = ["产品质量不错", "服务态度恶劣"]
        predictions = classifier.predict(test_texts)
        
        for text, pred in zip(test_texts, predictions):
            sentiment = "正面" if pred == 1 else "负面"
            print(f"'{text}' -> {sentiment}")
        
        return classifier

class AdvancedNLP:
    """高级NLP技术"""
    
    def transformer_models(self):
        """Transformer模型介绍"""
        print("\n=== Transformer模型 ===")
        
        # 使用Hugging Face transformers
        try:
            # 中文情感分析
            sentiment_analyzer = pipeline(
                "sentiment-analysis",
                model="uer/roberta-base-finetuned-chinanews-chinese"
            )
            
            # 文本分类示例
            texts = ["今天天气真好", "这个电影太无聊了"]
            results = sentiment_analyzer(texts)
            
            for text, result in zip(texts, results):
                print(f"'{text}' -> {result['label']} (置信度: {result['score']:.4f})")
                
        except Exception as e:
            print(f"Transformer模型加载失败: {e}")
            print("请先安装: pip install transformers torch")
    
    def named_entity_recognition(self):
        """命名实体识别"""
        print("\n=== 命名实体识别 ===")
        
        try:
            # 使用预训练的NER模型
            ner_pipeline = pipeline("ner", 
                                  model="dbmdz/bert-large-cased-finetuned-conll03-english")
            
            text = "Apple Inc. was founded by Steve Jobs in California."
            entities = ner_pipeline(text)
            
            print(f"原文本: {text}")
            print("识别的实体:")
            for entity in entities:
                print(f"  {entity['word']}: {entity['entity']}")
                
        except Exception as e:
            print(f"NER模型加载失败: {e}")

class NLPApplications:
    """NLP实际应用"""
    
    def chatbot_demo(self):
        """聊天机器人基础"""
        print("\n=== 简单聊天机器人 ===")
        
        class SimpleChatbot:
            def __init__(self):
                self.responses = {
                    "你好": "你好！有什么我可以帮助你的吗？",
                    "再见": "再见！祝你有美好的一天！",
                    "谢谢": "不客气！",
                    "默认": "我不太明白你的意思，能换个说法吗？"
                }
            
            def get_response(self, user_input):
                # 简单的关键词匹配
                for key in self.responses:
                    if key in user_input:
                        return self.responses[key]
                return self.responses["默认"]
        
        bot = SimpleChatbot()
        
        # 演示对话
        test_inputs = ["你好", "谢谢你", "今天天气怎么样"]
        for inp in test_inputs:
            response = bot.get_response(inp)
            print(f"用户: {inp}")
            print(f"机器人: {response}\n")
    
    def text_similarity(self):
        """文本相似度计算"""
        print("\n=== 文本相似度计算 ===")
        
        from sklearn.metrics.pairwise import cosine_similarity
        
        texts = [
            "人工智能是未来的趋势",
            "机器学习是AI的重要分支",
            "今天天气很好"
        ]
        
        # TF-IDF向量化
        vectorizer = TfidfVectorizer()
        tfidf_matrix = vectorizer.fit_transform(texts)
        
        # 计算余弦相似度
        similarity_matrix = cosine_similarity(tfidf_matrix)
        
        print("文本相似度矩阵:")
        for i, text1 in enumerate(texts):
            for j, text2 in enumerate(texts):
                if i < j:  # 避免重复计算
                    similarity = similarity_matrix[i][j]
                    print(f"'{text1}' 和 '{text2}': {similarity:.4f}")

def main():
    """主函数"""
    print("🤖 自然语言处理(NLP)基础教程")
    print("=" * 50)
    
    # 基础NLP
    nlp_basic = NLPFundamentals()
    
    # 文本预处理演示
    sample_text = "人工智能技术正在改变我们的世界"
    chinese_tokens, english_tokens = nlp_basic.text_preprocessing(sample_text)
    
    # 文本表示
    sample_texts = [
        "我喜欢学习新技术",
        "人工智能很有趣",
        "编程是我的爱好"
    ]
    tfidf_result = nlp_basic.text_representation(sample_texts)
    
    # 情感分析
    sentiment_classifier = nlp_basic.sentiment_analysis_demo()
    
    # 高级NLP
    advanced_nlp = AdvancedNLP()
    advanced_nlp.transformer_models()
    advanced_nlp.named_entity_recognition()
    
    # 应用示例
    applications = NLPApplications()
    applications.chatbot_demo()
    applications.text_similarity()
    
    print("\n✅ NLP基础学习完成！")

if __name__ == "__main__":
    main()