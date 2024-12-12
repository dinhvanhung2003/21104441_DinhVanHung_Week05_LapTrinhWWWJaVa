from flask import Flask, request, jsonify
from sqlalchemy import create_engine
import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
import logging
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart
import smtplib

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
def fetch_jobs_from_db1():
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
        jobs = fetch_jobs_from_db1()

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
# Hàm gửi email
def send_email(to_email, job_name):
    try:
        from_email = "dinhhung12122003@gmail.com"  # Thay bằng email của bạn
        from_password = "jelo kynd cjpn jhcw"  # Thay bằng mật khẩu ứng dụng của bạn

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
# Hàm lấy dữ liệu công việc từ DB
def fetch_jobs_from_db2():
    """Lấy dữ liệu công việc từ DB"""
    query = """
    SELECT 
        j.job_id, 
        j.job_name, 
        GROUP_CONCAT(js.skill_id, ':', js.skill_level SEPARATOR ',') AS job_skills
    FROM job j
    JOIN job_skill js ON j.job_id = js.job_id
    GROUP BY j.job_id, j.job_name;
    """
    return pd.read_sql(query, engine)


def fetch_candidates_from_db():
    """Lấy dữ liệu ứng viên từ DB"""
    query = """
    SELECT 
        c.id AS candidate_id, 
        c.full_name, 
        c.email AS candidate_email,
        GROUP_CONCAT(cs.skill_id, ':', cs.skill_level SEPARATOR ',') AS candidate_skills
    FROM candidate c
    JOIN candidate_skill cs ON c.id = cs.candidate_id
    GROUP BY c.id, c.full_name, c.email;
    """
    return pd.read_sql(query, engine)

def log_email(candidate_id, job_id, status, message=None):
    try:
        with engine.connect() as conn:
            query = """
            INSERT INTO email_log (candidate_id, job_id, status, message)
            VALUES (%s, %s, %s, %s)
            """
            conn.execute(query, (candidate_id, job_id, status, message))
        logging.info(f"Email log inserted: Candidate ID {candidate_id}, Job ID {job_id}, Status {status}")
    except Exception as e:
        logging.error(f"Failed to log email: {e}")




@app.route('/evaluate', methods=['POST'])
def evaluate_candidates():
    try:
        # Lấy dữ liệu công việc và ứng viên từ DB
        logging.info("Fetching jobs and candidates from the database...")
        jobs2 = fetch_jobs_from_db2()
        candidates = fetch_candidates_from_db()

        if jobs2.empty or candidates.empty:
            return jsonify({"error": "No jobs or candidates available for evaluation."}), 400

        logging.info("Transforming job skills into TF-IDF matrix...")
        tfidf = TfidfVectorizer()
        job_skills_matrix = tfidf.fit_transform(jobs2["job_skills"])

        invited_candidates = []

        for _, candidate in candidates.iterrows():
            logging.info(f"Evaluating candidate {candidate['full_name']}...")
            candidate_vector = tfidf.transform([candidate["candidate_skills"]])
            similarity_scores = cosine_similarity(candidate_vector, job_skills_matrix)
            jobs2["similarity"] = similarity_scores[0]

            best_match = jobs2.sort_values(by="similarity", ascending=False).iloc[0]
            logging.info(f"Best match for {candidate['full_name']}: {best_match['job_name']} with similarity {best_match['similarity']}")

            if best_match["similarity"] > 0.7:
                try:
                    send_email(candidate["email"], best_match["job_name"])
                    log_email(candidate["candidate_id"], best_match["job_id"], "SUCCESS")
                    invited_candidates.append({
                        "candidate_id": candidate["candidate_id"],
                        "candidate_name": candidate["full_name"],
                        "job_name": best_match["job_name"],
                        "similarity": best_match["similarity"]
                    })
                except Exception as e:
                    log_email(candidate["candidate_id"], best_match["job_id"], "FAILURE", str(e))

        logging.info("Evaluation completed.")
        return jsonify(invited_candidates)

    except Exception as e:
        logging.error(f"Error in candidate evaluation: {e}")
        return jsonify({"error": "An error occurred during evaluation."}), 500






if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)

