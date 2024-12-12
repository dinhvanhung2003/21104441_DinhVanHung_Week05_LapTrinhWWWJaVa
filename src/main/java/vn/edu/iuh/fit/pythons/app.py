from flask import Flask, request, jsonify
from sqlalchemy import create_engine
import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
import logging

# Cấu hình logging
logging.basicConfig(level=logging.DEBUG)

app = Flask(__name__)

# Cấu hình kết nối tới MariaDB
DB_USERNAME = 'root'
DB_PASSWORD = '123'
DB_HOST = 'localhost'
DB_PORT = '3306'
DB_NAME = 'works'

# Tạo kết nối database
engine = create_engine(f'mysql+pymysql://{DB_USERNAME}:{DB_PASSWORD}@{DB_HOST}:{DB_PORT}/{DB_NAME}')

# Hàm lấy dữ liệu công việc từ DB
def fetch_jobs_from_db():
    try:
        query = """
        SELECT job_id, job_name, job_skills
        FROM job
        WHERE job_skills IS NOT NULL AND job_skills != '';
        """
        jobs_df = pd.read_sql(query, engine)

        # Kiểm tra dữ liệu
        if jobs_df.empty or 'job_skills' not in jobs_df.columns:
            raise ValueError("Job skills data is invalid.")

        return jobs_df

    except Exception as e:
        logging.error(f"Error fetching jobs from database: {e}")
        return pd.DataFrame()

@app.route('/recommend', methods=['POST'])
def recommend_jobs():
    try:
        # Lấy dữ liệu công việc từ DB
        jobs = fetch_jobs_from_db()

        # Kiểm tra job_skills
        if jobs.empty or 'job_skills' not in jobs.columns:
            return jsonify({"error": "Job skills data is invalid."}), 400

        # Xử lý TF-IDF
        tfidf = TfidfVectorizer()
        job_skills_matrix = tfidf.fit_transform(jobs["job_skills"])

        # Lấy kỹ năng ứng viên từ request
        data = request.get_json()
        candidate_skills = data.get("candidate_skills", "")
        if not candidate_skills:
            return jsonify({"error": "Candidate skills are missing."}), 400

        # Mã hóa kỹ năng ứng viên và tính độ tương đồng
        candidate_vector = tfidf.transform([candidate_skills])
        similarity_scores = cosine_similarity(candidate_vector, job_skills_matrix)

        # Gán điểm tương đồng
        jobs["similarity"] = similarity_scores[0]
        recommended_jobs = jobs.sort_values(by="similarity", ascending=False)

        # Trả kết quả
        return jsonify(recommended_jobs[["job_id", "job_name", "similarity"]].to_dict(orient="records"))

    except Exception as e:
        logging.error(f"Error in recommendation: {e}")
        return jsonify({"error": "An error occurred while processing the recommendation."}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
