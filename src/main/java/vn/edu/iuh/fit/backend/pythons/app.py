from flask import Flask, request, jsonify
from sqlalchemy import create_engine, text
import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
import logging
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart
import smtplib
from datetime import datetime

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
engine = create_engine(f"mysql+pymysql://{DB_USERNAME}:{DB_PASSWORD}@{DB_HOST}:{DB_PORT}/{DB_NAME}")

# Hàm gửi email
def send_email(to_email, job_name):
    try:
        from_email = "dinhhung12122003@gmail.com"
        from_password = "jelo kynd cjpn jhcw"

        subject = "Lời mời làm việc"
        body = f"Chúc mừng! Bạn đã được mời làm việc cho công việc: {job_name}. Vui lòng liên hệ lại để biết thêm chi tiết."

        msg = MIMEMultipart()
        msg['From'] = from_email
        msg['To'] = to_email
        msg['Subject'] = subject
        msg.attach(MIMEText(body, 'plain'))

        with smtplib.SMTP('smtp.gmail.com', 587) as server:
            server.starttls()
            server.login(from_email, from_password)
            server.sendmail(from_email, to_email, msg.as_string())

        logging.info(f"Email sent to {to_email}")
    except Exception as e:
        logging.error(f"Failed to send email to {to_email}: {e}")

# Hàm log email vào database
def log_email(candidate_id, job_id, status, message=None):
    try:
        query = text("""
            INSERT INTO email_log (candidate_id, job_id, status, message, sent_at)
            VALUES (:candidate_id, :job_id, :status, :message, :sent_at)
        """)
        with engine.connect() as conn:
            conn.execute(query, {
                "candidate_id": candidate_id,
                "job_id": job_id,
                "status": status,
                "message": message,
                "sent_at": datetime.now()
            })
            conn.commit()
        logging.info(f"Logged email for Candidate ID: {candidate_id}, Job ID: {job_id}, Status: {status}")
    except Exception as e:
        logging.error(f"Failed to log email: {e}")

# Hàm lấy thông tin công việc từ DB
def fetch_jobs_from_db1():
    query = """
        SELECT job_id, job_name, job_skills
        FROM job
        WHERE job_skills IS NOT NULL AND job_skills != '';
    """
    return pd.read_sql(query, engine)

# API gợi ý công việc
@app.route('/recommend', methods=['POST'])
def recommend_jobs():
    try:
        # Lấy dữ liệu công việc từ DB
        jobs = fetch_jobs_from_db1()

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

# Hàm lấy thông tin công việc theo job_id
def fetch_job_by_id(job_id):
    query = """
        SELECT 
            j.job_id, 
            j.job_name, 
            GROUP_CONCAT(js.skill_id, ':', js.skill_level SEPARATOR ',') AS job_skills
        FROM job j
        JOIN job_skill js ON j.job_id = js.job_id
        WHERE j.job_id = %(job_id)s
        GROUP BY j.job_id, j.job_name;
    """
    return pd.read_sql(query, engine, params={"job_id": job_id})

# Hàm lấy ứng viên đã ứng tuyển vào công việc cụ thể
def fetch_candidates_for_job(job_id):
    """Lấy dữ liệu ứng viên đã ứng tuyển cho công việc cụ thể"""
    query = """
    SELECT 
        c.id AS candidate_id, 
        c.full_name, 
        c.email AS candidate_email,
        GROUP_CONCAT(cs.skill_id, ':', cs.skill_level SEPARATOR ',') AS candidate_skills
    FROM candidate c
    JOIN candidate_skill cs ON c.id = cs.candidate_id
    JOIN candidate_job cj ON c.id = cj.candidate_id
    WHERE cj.job_id = %s
    GROUP BY c.id, c.full_name, c.email;
    """
    # Sử dụng `%s` và truyền tham số dưới dạng tuple
    return pd.read_sql(query, engine, params=(job_id,))



@app.route('/evaluate', methods=['POST'])
def evaluate_candidates():
    try:
        # Lấy job_id từ request payload
        data = request.get_json()
        job_id = data.get("job_id")
        if not job_id:
            return jsonify({"error": "job_id is required"}), 400

        logging.info(f"Fetching job {job_id} and candidates from the database...")

        # Lấy công việc cụ thể theo job_id
        query_job = """
        SELECT 
            j.job_id, 
            j.job_name, 
            GROUP_CONCAT(js.skill_id, ':', js.skill_level SEPARATOR ',') AS job_skills
        FROM job j
        JOIN job_skill js ON j.job_id = js.job_id
        WHERE j.job_id = %s
        GROUP BY j.job_id, j.job_name;
        """
        jobs2 = pd.read_sql(query_job, engine, params=(job_id,))  # Sử dụng `%s` và tuple (job_id,)

        # Lấy danh sách ứng viên chỉ cho công việc này
        candidates = fetch_candidates_for_job(job_id)

        # Kiểm tra dữ liệu
        if jobs2.empty:
            return jsonify({"error": f"No job found with job_id {job_id}."}), 404
        if candidates.empty:
            return jsonify({"error": "No candidates applied for this job."}), 400

        # TF-IDF và đánh giá tương đồng
        tfidf = TfidfVectorizer()
        job_skills_matrix = tfidf.fit_transform(jobs2["job_skills"])

        invited_candidates = []

        for _, candidate in candidates.iterrows():
            try:
                logging.info(f"Evaluating candidate {candidate['full_name']}...")

                candidate_vector = tfidf.transform([candidate["candidate_skills"]])
                similarity_scores = cosine_similarity(candidate_vector, job_skills_matrix)

                best_match = jobs2.iloc[0]
                if similarity_scores[0][0] > 0.7:
                    send_email(candidate["candidate_email"], best_match["job_name"])
                    log_email(candidate["candidate_id"], best_match["job_id"], "SUCCESS")
                    invited_candidates.append({
                        "candidate_id": candidate["candidate_id"],
                        "candidate_name": candidate["full_name"],
                        "job_name": best_match["job_name"],
                        "similarity": similarity_scores[0][0]
                    })
                else:
                    log_email(candidate["candidate_id"], job_id, "FAILURE", "Low similarity")
            except Exception as e:
                log_email(candidate["candidate_id"], job_id, "FAILURE", str(e))

        return jsonify(invited_candidates)

    except Exception as e:
        logging.error(f"Error in candidate evaluation: {e}")
        return jsonify({"error": "An error occurred during evaluation."}), 500





if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
