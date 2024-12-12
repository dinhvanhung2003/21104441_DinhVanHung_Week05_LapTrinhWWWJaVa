import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity

# Dữ liệu ví dụ
candidates = {
    "candidate_id": [1],
    "candidate_skills": ["java, spring boot, reactjs, docker"]
}

jobs = {
    "job_id": [101, 102, 103],
    "job_name": ["Software Engineer", "Frontend Developer", "DevOps Engineer"],
    "job_skills": [
        "java, spring boot, docker",
        "reactjs, javascript, css",
        "docker, kubernetes, aws"
    ]
}

# Chuyển dữ liệu thành DataFrame
df_candidates = pd.DataFrame(candidates)
df_jobs = pd.DataFrame(jobs)

# Sử dụng TF-IDF để mã hóa kỹ năng
tfidf = TfidfVectorizer()
candidate_skills_matrix = tfidf.fit_transform(df_candidates["candidate_skills"])
job_skills_matrix = tfidf.transform(df_jobs["job_skills"])

# Tính toán độ tương đồng
similarity_scores = cosine_similarity(candidate_skills_matrix, job_skills_matrix)

# Gán điểm tương đồng vào DataFrame
df_jobs["similarity"] = similarity_scores[0]

# Sắp xếp công việc theo mức độ phù hợp
recommended_jobs = df_jobs.sort_values(by="similarity", ascending=False)
print(recommended_jobs[["job_id", "job_name", "similarity"]])
