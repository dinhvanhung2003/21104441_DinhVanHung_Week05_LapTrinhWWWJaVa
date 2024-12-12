from flask import Flask, request, jsonify
import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity

app = Flask(__name__)

# Dữ liệu công việc mẫu
jobs = pd.DataFrame({
    "job_id": [101, 102, 103, 104],
    "job_name": ["Software Engineer", "Frontend Developer", "DevOps Engineer", "Data Scientist"],
    "job_skills": [
        "java, spring boot, docker",
        "reactjs, javascript, css",
        "docker, kubernetes, aws",
        "python, machine learning, data analysis"
    ]
})

# Xử lý dữ liệu và tạo TF-IDF vectorizer
def preprocess_skills(skills):
    """Chuyển kỹ năng thành dạng chuẩn hóa (lowercase, loại bỏ khoảng trắng thừa)."""
    return skills.lower().replace(",", " ").strip()

jobs["job_skills"] = jobs["job_skills"].apply(preprocess_skills)

# Tạo TF-IDF vectorizer từ dữ liệu kỹ năng công việc
tfidf = TfidfVectorizer()
job_skills_matrix = tfidf.fit_transform(jobs["job_skills"])

@app.route('/recommend', methods=['POST'])
def recommend_jobs():
    try:
        data = request.get_json()
        candidate_skills = data.get("candidate_skills", "")

        if not candidate_skills:
            return jsonify({"error": "No candidate skills provided"}), 400

        # Xử lý kỹ năng ứng viên
        candidate_skills = preprocess_skills(candidate_skills)
        candidate_vector = tfidf.transform([candidate_skills])

        # Tính toán điểm tương đồng
        similarity_scores = cosine_similarity(candidate_vector, job_skills_matrix)

        # Gắn điểm tương đồng vào DataFrame
        jobs["similarity"] = similarity_scores[0]
        recommended_jobs = jobs.sort_values(by="similarity", ascending=False).head(5)

        # Trả về kết quả
        return jsonify(recommended_jobs[["job_id", "job_name", "similarity"]].to_dict(orient="records"))

    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
