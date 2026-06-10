import os
import tempfile
import re
from fastapi import UploadFile, File, Form
from docx import Document
from fastapi import FastAPI, HTTPException, Request
from fastapi.middleware.cors import CORSMiddleware
import psycopg2
from psycopg2.extras import RealDictCursor
from pydantic import BaseModel
from typing import List, Optional
from datetime import datetime
from deep_translator import GoogleTranslator
from pathlib import Path
from gigachat_utils import extract_full_from_syllabus   # <-- новая функция, возвращает topics + standard
import json   # если нет импорта json в начале, добавь   # <-- импорт из GigaChat
import PyPDF2

# ========== ПЕРЕВОДЧИК ==========

def extract_text_from_file(file_path: str, ext: str) -> str:
    """Извлекает текст из .docx или .pdf файла"""
    try:
        if ext == '.docx':
            from docx import Document
            doc = Document(file_path)
            return "\n".join([p.text for p in doc.paragraphs])
        elif ext == '.pdf':
            with open(file_path, 'rb') as f:
                reader = PyPDF2.PdfReader(f)
                text = ""
                for page in reader.pages:
                    page_text = page.extract_text()
                    if page_text:
                        text += page_text + "\n"
                return text
        else:
            return ""
    except Exception as e:
        print(f"Error extracting text: {e}")
        return ""

def translate_text(text: str, target_lang: str = "ru") -> str:
    """Автоматический перевод текста на русский язык"""
    if not text:
        return text
    try:
        translator = GoogleTranslator(source='auto', target=target_lang)
        translated = translator.translate(text)
        return translated
    except Exception as e:
        print(f"Translation error: {e}")
        return text  # Если ошибка, возвращаем оригинал

app = FastAPI()

# Разрешаем CORS для Android-приложения
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# ========== МОДЕЛИ ДАННЫХ ==========

class User(BaseModel):
    id: Optional[int] = None
    email: str
    password: str
    first_name: str
    last_name: str
    country: str
    user_type: str
    university: str
    created_at: Optional[datetime] = None

class Course(BaseModel):
    id: Optional[int] = None
    title: str
    subject: Optional[str] = None
    description: Optional[str] = None
    image_url: Optional[str] = None
    course_number: Optional[int] = None
    creator_id: Optional[int] = None
    duration: Optional[int] = None
    rating: Optional[float] = None
    students_count: Optional[int] = None
    created_at: Optional[datetime] = None

class UserCourse(BaseModel):
    id: Optional[int] = None
    user_id: int
    course_id: int
    status: str
    result: Optional[float] = None
    started_at: Optional[str] = None
    completed_at: Optional[str] = None

class TestResult(BaseModel):
    user_id: int
    test_id: int
    score: int
    total_questions: int
    percentage: int
    passed: bool

# ========== МОДЕЛИ ДЛЯ СОЗДАНИЯ КУРСОВ ==========

class CourseCreate(BaseModel):
    title: str
    subject: Optional[str] = None
    description: Optional[str] = None
    image_url: Optional[str] = None
    course_number: int
    duration: int
    creator_id: int

class LessonCreate(BaseModel):
    course_id: int
    title: str
    description: Optional[str] = None
    video_url: Optional[str] = None
    text_content: Optional[str] = None
    order: int
    duration: int
    is_brics: bool = False  # ДОБАВИТЬ

class QuestionCreate(BaseModel):
    test_id: int
    question_text: str
    option_a: str
    option_b: str
    option_c: str
    option_d: str
    correct_answer: str  # 'A', 'B', 'C', или 'D'
    points: int = 10
    order: int = 0

# ========== ПОДКЛЮЧЕНИЕ К БД ==========

def get_db_connection():
    conn = psycopg2.connect(
        host="localhost",
        port="5432",
        database="mobile_app_db",
        user="app_user",
        password="111"
    )
    return conn

# ========== ПАРСЕР SYLLABUS ==========

@app.post("/rest/v1/import_syllabus")
async def import_syllabus_ai(
    course_id: int = Form(...),
    file: UploadFile = File(...),
    auto_translate: bool = Form(True)
):
    # 1. Сохраняем временный файл
    ext = Path(file.filename).suffix.lower()
    if ext not in ('.docx', '.pdf'):
        raise HTTPException(400, "Only .docx or .pdf allowed")
    
    with tempfile.NamedTemporaryFile(delete=False, suffix=ext) as tmp:
        content = await file.read()
        tmp.write(content)
        tmp_path = tmp.name

    # 2. Извлекаем текст из документа
    raw_text = extract_text_from_file(tmp_path, ext)
    os.unlink(tmp_path)

    if not raw_text:
        raise HTTPException(400, "Could not extract text from file")

    # 3. Используем GigaChat для выделения тем и стандарта (один запрос)
    data = extract_full_from_syllabus(raw_text)
    topics = data.get("topics", [])
    standard = data.get("standard", {})

    if not topics:
        raise HTTPException(400, "GigaChat did not return any topics")

    # 4. Создаём уроки в БД (с возможным переводом)
    conn = get_db_connection()
    cur = conn.cursor()
    created = []
    for topic in topics:
        title = topic.get("title", "Тема")
        description = topic.get("description", "")
        if auto_translate:
            title = translate_text(title, "ru")
            description = translate_text(description, "ru")
        cur.execute("""
            INSERT INTO lessons (course_id, title, description, "order", text_content, duration, created_at)
            VALUES (%s, %s, %s, %s, %s, %s, NOW())
            RETURNING id
        """, (course_id, title, description, topic["order"], f"# {title}\n\n{description}", 60))
        lesson_id = cur.fetchone()[0]
        created.append({"id": lesson_id, "order": topic["order"], "title": title})

    # 5. Если удалось распознать стандарт — сохраняем в profile курса
    if standard and any(standard.values()):   # если не пустой
        cur.execute("""
            UPDATE courses SET profile = %s WHERE id = %s
        """, (json.dumps(standard, ensure_ascii=False), course_id))

    conn.commit()
    cur.close()
    conn.close()

    return {
        "success": True,
        "lessons_created": len(created),
        "lessons": created,
        "standard_detected": standard   # полезно для отладки
    }

# ========== ПАРСЕР SYLLABUS эквивалентные компетенции ==========



# ========== ПОЛЬЗОВАТЕЛИ ==========

@app.post("/rest/v1/Users")
async def register_user(user: User):
    try:
        conn = get_db_connection()
        cur = conn.cursor()
        cur.execute("""
            INSERT INTO Users (email, password, first_name, last_name, country, user_type, university)
            VALUES (%s, %s, %s, %s, %s, %s, %s)
            RETURNING id
        """, (user.email, user.password, user.first_name, user.last_name, user.country, user.user_type, user.university))
        user_id = cur.fetchone()[0]
        conn.commit()
        cur.close()
        conn.close()
        return {"id": user_id, "message": "User created"}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/rest/v1/Users")
async def check_email(email: str = None):
    try:
        conn = get_db_connection()
        cur = conn.cursor(cursor_factory=RealDictCursor)
        if email:
            cur.execute("SELECT * FROM Users WHERE email = %s", (email,))
        else:
            cur.execute("SELECT * FROM Users")
        users = cur.fetchall()
        cur.close()
        conn.close()
        return users
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))



# ========== КУРСЫ ==========

@app.get("/rest/v1/courses_with_creators")
async def get_courses_with_creators():
    try:
        conn = get_db_connection()
        cur = conn.cursor(cursor_factory=RealDictCursor)
        cur.execute("""
            SELECT 
                c.*,
                u.first_name AS creator_first_name,
                u.last_name AS creator_last_name,
                u.country AS creator_country,
                u.university AS creator_university,
                u.user_type AS creator_type
            FROM courses c
            LEFT JOIN users u ON c.creator_id = u.id
            ORDER BY c.id;
        """)
        courses = cur.fetchall()
        cur.close()
        conn.close()
        return courses
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/rest/v1/courses")
async def get_courses(id: int = None, course_number: int = None, subject: str = None):
    try:
        conn = get_db_connection()
        cur = conn.cursor(cursor_factory=RealDictCursor)
        if id:
            cur.execute("""
                SELECT 
                    c.*,
                    u.first_name AS creator_first_name,
                    u.last_name AS creator_last_name,
                    u.country AS creator_country,
                    u.university AS creator_university,
                    u.user_type AS creator_type
                FROM courses c
                LEFT JOIN users u ON c.creator_id = u.id
                WHERE c.id = %s
            """, (id,))
        elif course_number:
            cur.execute("""
                SELECT 
                    c.*,
                    u.first_name AS creator_first_name,
                    u.last_name AS creator_last_name,
                    u.country AS creator_country,
                    u.university AS creator_university,
                    u.user_type AS creator_type
                FROM courses c
                LEFT JOIN users u ON c.creator_id = u.id
                WHERE c.course_number = %s
            """, (course_number,))
        elif subject:
            cur.execute("""
                SELECT 
                    c.*,
                    u.first_name AS creator_first_name,
                    u.last_name AS creator_last_name,
                    u.country AS creator_country,
                    u.university AS creator_university,
                    u.user_type AS creator_type
                FROM courses c
                LEFT JOIN users u ON c.creator_id = u.id
                WHERE c.subject = %s
            """, (subject,))
        else:
            cur.execute("""
                SELECT 
                    c.*,
                    u.first_name AS creator_first_name,
                    u.last_name AS creator_last_name,
                    u.country AS creator_country,
                    u.university AS creator_university,
                    u.user_type AS creator_type
                FROM courses c
                LEFT JOIN users u ON c.creator_id = u.id
                ORDER BY c.id;
            """)
        courses = cur.fetchall()
        cur.close()
        conn.close()
        return courses
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# ========== USER_COURSES ==========

@app.get("/rest/v1/user_courses")
async def get_user_courses(user_id: int = None, course_id: int = None):
    try:
        conn = get_db_connection()
        cur = conn.cursor(cursor_factory=RealDictCursor)
        
        if user_id and course_id:
            cur.execute("""
                SELECT 
                    uc.*,
                    c.id AS course_id_alias,
                    c.title,
                    c.subject,
                    c.description,
                    c.image_url,
                    c.course_number,
                    c.duration,
                    c.rating,
                    c.students_count,
                    c.creator_id,
                    u.first_name AS creator_first_name,
                    u.last_name AS creator_last_name,
                    u.country AS creator_country,
                    u.university AS creator_university,
                    u.user_type AS creator_type
                FROM user_courses uc
                JOIN courses c ON uc.course_id = c.id
                LEFT JOIN users u ON c.creator_id = u.id
                WHERE uc.user_id = %s AND uc.course_id = %s
            """, (user_id, course_id))
        elif user_id:
            cur.execute("""
                SELECT 
                    uc.*,
                    c.id AS course_id_alias,
                    c.title,
                    c.subject,
                    c.description,
                    c.image_url,
                    c.course_number,
                    c.duration,
                    c.rating,
                    c.students_count,
                    c.creator_id,
                    u.first_name AS creator_first_name,
                    u.last_name AS creator_last_name,
                    u.country AS creator_country,
                    u.university AS creator_university,
                    u.user_type AS creator_type
                FROM user_courses uc
                JOIN courses c ON uc.course_id = c.id
                LEFT JOIN users u ON c.creator_id = u.id
                WHERE uc.user_id = %s
                ORDER BY uc.created_at DESC
            """, (user_id,))
        else:
            cur.execute("""
                SELECT 
                    uc.*,
                    c.id AS course_id_alias,
                    c.title,
                    c.subject,
                    c.description,
                    c.image_url,
                    c.course_number,
                    c.duration,
                    c.rating,
                    c.students_count,
                    c.creator_id,
                    u.first_name AS creator_first_name,
                    u.last_name AS creator_last_name,
                    u.country AS creator_country,
                    u.university AS creator_university,
                    u.user_type AS creator_type
                FROM user_courses uc
                JOIN courses c ON uc.course_id = c.id
                LEFT JOIN users u ON c.creator_id = u.id
                ORDER BY uc.created_at DESC
            """)
        
        results = cur.fetchall()
        cur.close()
        conn.close()
        return results
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/rest/v1/user_courses")
async def add_user_course(request: Request):
    try:
        body = await request.json()
        conn = get_db_connection()
        cur = conn.cursor()
        
        cur.execute("""
            INSERT INTO user_courses (user_id, course_id, status)
            VALUES (%s, %s, %s)
            ON CONFLICT (user_id, course_id) DO NOTHING
            RETURNING id
        """, (body.get('user_id'), body.get('course_id'), body.get('status', 'saved')))
        
        new_id = cur.fetchone()
        conn.commit()
        cur.close()
        conn.close()
        
        return {"id": new_id[0] if new_id else None, "message": "Course added"}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.patch("/rest/v1/user_courses")
async def update_user_course(
    user_id: int,
    course_id: int,
    result: int,
    status: str = "completed",
    completed_at: str = None
):
    try:
        if not completed_at:
            completed_at = datetime.now().isoformat()
        
        conn = get_db_connection()
        cur = conn.cursor()
        
        cur.execute("""
            UPDATE user_courses 
            SET result = %s, status = %s, completed_at = %s, updated_at = NOW()
            WHERE user_id = %s AND course_id = %s
            RETURNING id
        """, (result, status, completed_at, user_id, course_id))
        
        updated = cur.fetchone()
        conn.commit()
        cur.close()
        conn.close()
        
        return {"success": True, "updated": updated is not None}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# ========== УРОКИ ==========

@app.get("/rest/v1/lessons")
async def get_course_lessons(course_id: int = None):
    try:
        conn = get_db_connection()
        cur = conn.cursor(cursor_factory=RealDictCursor)
        
        if course_id:
            cur.execute("""
                SELECT 
                    l.*,
                    cc.id IS NOT NULL AS is_contribution,
                    u.first_name AS contributor_first_name,
                    u.last_name AS contributor_last_name,
                    u.country AS contributor_country
                FROM lessons l
                LEFT JOIN course_contributions cc ON l.id = cc.lesson_id
                LEFT JOIN users u ON cc.contributor_id = u.id
                WHERE l.course_id = %s 
                ORDER BY l."order"
            """, (course_id,))
        else:
            cur.execute("""
                SELECT 
                    l.*,
                    cc.id IS NOT NULL AS is_contribution,
                    u.first_name AS contributor_first_name,
                    u.last_name AS contributor_last_name,
                    u.country AS contributor_country
                FROM lessons l
                LEFT JOIN course_contributions cc ON l.id = cc.lesson_id
                LEFT JOIN users u ON cc.contributor_id = u.id
                ORDER BY l.course_id, l."order"
            """)
        
        lessons = cur.fetchall()
        cur.close()
        conn.close()
        return lessons
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# ========== ПРОГРЕСС ПОЛЬЗОВАТЕЛЯ ==========

@app.get("/rest/v1/user_lesson_progress")
async def get_user_lesson_progress(user_id: int = None, course_id: int = None):
    try:
        conn = get_db_connection()
        cur = conn.cursor()
        
        if user_id and course_id:
            cur.execute("""
                SELECT ulp.lesson_id 
                FROM user_lesson_progress ulp
                JOIN lessons l ON ulp.lesson_id = l.id
                WHERE ulp.user_id = %s 
                    AND l.course_id = %s 
                    AND ulp.is_completed = true
            """, (user_id, course_id))
        else:
            return []
        
        result = [row[0] for row in cur.fetchall()]
        cur.close()
        conn.close()
        return result
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/rest/v1/lessons/{lesson_id}/complete")
async def complete_lesson(lesson_id: int, user_id: int):
    try:
        conn = get_db_connection()
        cur = conn.cursor()
        
        cur.execute("""
            INSERT INTO user_lesson_progress (user_id, lesson_id, is_completed, completed_at, updated_at)
            VALUES (%s, %s, true, NOW(), NOW())
            ON CONFLICT (user_id, lesson_id) 
            DO UPDATE SET 
                is_completed = true,
                completed_at = NOW(),
                updated_at = NOW()
            RETURNING id
        """, (user_id, lesson_id))
        
        result = cur.fetchone()
        conn.commit()
        cur.close()
        conn.close()
        
        return {"success": True, "id": result[0] if result else None}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# ========== СНЯТИЕ ОТМЕТКИ С УРОКА (НОВЫЙ ЭНДПОИНТ) ==========

@app.delete("/rest/v1/lessons/{lesson_id}/progress")
async def uncomplete_lesson(lesson_id: int, user_id: int):
    try:
        conn = get_db_connection()
        cur = conn.cursor()
        
        cur.execute("""
            DELETE FROM user_lesson_progress
            WHERE user_id = %s AND lesson_id = %s
            RETURNING id
        """, (user_id, lesson_id))
        
        deleted = cur.fetchone()
        conn.commit()
        cur.close()
        conn.close()
        
        return {"success": True, "deleted": deleted is not None}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# ========== ТЕСТЫ ==========

@app.get("/rest/v1/course_tests")
async def get_course_test(course_id: int = None):
    try:
        conn = get_db_connection()
        cur = conn.cursor(cursor_factory=RealDictCursor)
        
        if course_id:
            cur.execute("SELECT id FROM course_tests WHERE course_id = %s", (course_id,))
            result = cur.fetchone()
            test_id = result['id'] if result else None
        else:
            cur.execute("SELECT id FROM course_tests")
            result = cur.fetchone()
            test_id = result['id'] if result else None
        
        cur.close()
        conn.close()
        return test_id
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/rest/v1/test_questions")
async def get_test_questions(test_id: int = None):
    try:
        conn = get_db_connection()
        cur = conn.cursor(cursor_factory=RealDictCursor)
        
        if test_id:
            cur.execute("""
                SELECT * FROM test_questions 
                WHERE test_id = %s 
                ORDER BY "order"
            """, (test_id,))
        else:
            cur.execute("SELECT * FROM test_questions ORDER BY test_id, \"order\"")
        
        questions = cur.fetchall()
        cur.close()
        conn.close()
        return questions
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/rest/v1/user_test_results")
async def submit_test_result(request: Request):
    try:
        body = await request.json()
        conn = get_db_connection()
        cur = conn.cursor()
        
        cur.execute("""
            INSERT INTO user_test_results (user_id, test_id, score, total_questions, percentage, passed, completed_at)
            VALUES (%s, %s, %s, %s, %s, %s, NOW())
            ON CONFLICT (user_id, test_id) 
            DO UPDATE SET 
                score = EXCLUDED.score,
                total_questions = EXCLUDED.total_questions,
                percentage = EXCLUDED.percentage,
                passed = EXCLUDED.passed,
                completed_at = NOW()
            RETURNING id
        """, (
            body.get('user_id'),
            body.get('test_id'),
            body.get('score'),
            body.get('total_questions'),
            body.get('percentage'),
            body.get('passed')
        ))
        
        result = cur.fetchone()
        conn.commit()
        cur.close()
        conn.close()
        
        return {"success": True, "id": result[0] if result else None}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# ========== СОЗДАНИЕ КУРСОВ (ДЛЯ ПРЕПОДАВАТЕЛЕЙ) ==========

@app.post("/rest/v1/courses")
async def create_course(course: CourseCreate):
    try:
        conn = get_db_connection()
        cur = conn.cursor()
        
        cur.execute("""
            INSERT INTO courses (title, subject, description, image_url, course_number, creator_id, duration, rating, students_count, created_at)
            VALUES (%s, %s, %s, %s, %s, %s, %s, 0, 0, NOW())
            RETURNING id
        """, (course.title, course.subject, course.description, course.image_url, 
              course.course_number, course.creator_id, course.duration))
        
        course_id = cur.fetchone()[0]
        conn.commit()
        cur.close()
        conn.close()
        
        return {"id": course_id, "message": "Course created successfully"}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))



# Добавление урока

@app.post("/rest/v1/lessons")
async def create_lesson(lesson: LessonCreate):
    try:
        conn = get_db_connection()
        cur = conn.cursor()
        
        cur.execute("""
            INSERT INTO lessons (course_id, title, description, video_url, text_content, "order", duration, is_brics, created_at)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, NOW())
            RETURNING id
        """, (lesson.course_id, lesson.title, lesson.description, lesson.video_url, 
              lesson.text_content, lesson.order, lesson.duration, lesson.is_brics))
        
        lesson_id = cur.fetchone()[0]
        conn.commit()
        cur.close()
        conn.close()
        
        return {"id": lesson_id, "message": "Lesson created successfully"}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.patch("/rest/v1/lessons/{lesson_id}")
async def update_lesson(lesson_id: int, request: Request):
    try:
        body = await request.json()
        print(f"Обновление урока {lesson_id}: {body}")

        conn = get_db_connection()
        cur = conn.cursor()

        cur.execute("""
            UPDATE lessons 
            SET title = %s, text_content = %s, video_url = %s, duration = %s
            WHERE id = %s
            RETURNING id
        """, (
            body.get('title'),
            body.get('text_content'),   # было 'textContent'
            body.get('video_url'),      # было 'videoUrl'
            body.get('duration'),
            lesson_id
        ))

        updated = cur.fetchone()
        conn.commit()
        cur.close()
        conn.close()

        if updated:
            print(f"✅ Урок {lesson_id} обновлён")
            return {"success": True, "id": updated[0]}
        else:
            raise HTTPException(status_code=404, detail="Lesson not found")
    except Exception as e:
        print(f"❌ Ошибка: {e}")
        raise HTTPException(status_code=500, detail=str(e))

# Создание теста для курса

@app.post("/rest/v1/course_tests")
async def create_course_test(course_id: int, title: str = "Финальный тест", passing_score: int = 70):
    try:
        conn = get_db_connection()
        cur = conn.cursor()
        
        # Сначала проверяем, есть ли уже тест для этого курса
        cur.execute("SELECT id FROM course_tests WHERE course_id = %s", (course_id,))
        existing = cur.fetchone()
        
        if existing:
            cur.close()
            conn.close()
            return {"id": existing[0], "message": "Test already exists for this course"}
        
        # Если теста нет - создаем новый (БЕЗ указания id!)
        cur.execute("""
            INSERT INTO course_tests (course_id, title, passing_score, created_at)
            VALUES (%s, %s, %s, NOW())
            RETURNING id
        """, (course_id, title, passing_score))
        
        result = cur.fetchone()
        conn.commit()
        cur.close()
        conn.close()
        
        return {"id": result[0], "message": "Test created successfully"}
        
    except Exception as e:
        # Логируем ошибку для отладки
        print(f"Error: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

# Добавление вопроса в тест 

@app.post("/rest/v1/test_questions")
async def create_question(question: QuestionCreate):
    try:
        conn = get_db_connection()
        cur = conn.cursor()
        
        cur.execute("""
            INSERT INTO test_questions (test_id, question_text, option_a, option_b, option_c, option_d, correct_answer, points, "order")
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)
            RETURNING id
        """, (question.test_id, question.question_text, question.option_a, question.option_b,
              question.option_c, question.option_d, question.correct_answer, question.points, question.order))
        
        question_id = cur.fetchone()[0]
        conn.commit()
        cur.close()
        conn.close()
        
        return {"id": question_id, "message": "Question created"}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# Получение всех курсов преподавателя

@app.get("/rest/v1/courses/teacher/{teacher_id}")
async def get_teacher_courses(teacher_id: int):
    try:
        conn = get_db_connection()
        cur = conn.cursor(cursor_factory=RealDictCursor)
        
        cur.execute("""
            SELECT * FROM courses
            WHERE creator_id = %s
            ORDER BY created_at DESC
        """, (teacher_id,))
        
        courses = cur.fetchall()
        cur.close()
        conn.close()
        return courses
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# ========== ТЕСТОВЫЙ ЭНДПОИНТ ==========

@app.get("/")
async def root():
    return {"message": "API работает! Доступные эндпоинты: /rest/v1/courses_with_creators"}

@app.get("/rest/v1/country_ranking")
async def get_country_ranking():
    try:
        conn = get_db_connection()
        cur = conn.cursor(cursor_factory=RealDictCursor)
        cur.execute("""
            SELECT 
                country,
                avg_score,
                total_users,
                total_tests,
                rank
            FROM country_ranking
            ORDER BY rank ASC
        """)
        ranking = cur.fetchall()
        cur.close()
        conn.close()
        return ranking
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/rest/v1/course_tests/{course_id}/check")
async def check_course_test(course_id: int):
    try:
        conn = get_db_connection()
        cur = conn.cursor(cursor_factory=RealDictCursor)
        cur.execute("SELECT id FROM course_tests WHERE course_id = %s", (course_id,))
        test = cur.fetchone()
        cur.close()
        conn.close()
        return {"exists": test is not None, "test_id": test["id"] if test else None}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# ========== ПРЕДЛОЖЕНИЯ ДОПОЛНЕНИЙ ОТ ПРЕПОДАВАТЕЛЕЙ ==========

from pydantic import BaseModel
from typing import Optional, List, Dict, Any

class SuggestionCreate(BaseModel):
    course_id: int
    title: str
    text_content: Optional[str] = None
    video_url: Optional[str] = None
    duration: Optional[str] = None

class SuggestionApprove(BaseModel):
    suggestion_id: int
    teacher_id: int

class SuggestionReject(BaseModel):
    suggestion_id: int
    teacher_id: int
    reason: Optional[str] = None

# 1. Отправить предложение дополнить курс
@app.post("/rest/v1/courses/{course_id}/suggestions")
async def send_suggestion(course_id: int, suggestion: SuggestionCreate, request: Request):
    try:
        # Получаем user_id из тела запроса (через request.json или через Body)
        body = await request.json()
        suggested_by = body.get('suggested_by')
        
        if not suggested_by:
            raise HTTPException(status_code=400, detail="suggested_by is required")
        
        conn = get_db_connection()
        cur = conn.cursor()
        
        # Проверяем, что пользователь не является создателем курса
        cur.execute("SELECT creator_id FROM courses WHERE id = %s", (course_id,))
        result = cur.fetchone()
        if not result:
            raise HTTPException(status_code=404, detail="Course not found")
        
        creator_id = result[0]
        if suggested_by == creator_id:
            raise HTTPException(status_code=400, detail="Нельзя предлагать уроки к своему курсу")
        
        # Вставляем предложение
        cur.execute("""
            INSERT INTO course_suggestions (course_id, suggested_by, title, text_content, video_url, duration, status, created_at)
            VALUES (%s, %s, %s, %s, %s, %s, 'pending', NOW())
            RETURNING id
        """, (course_id, suggested_by, suggestion.title, suggestion.text_content, 
              suggestion.video_url, suggestion.duration))
        
        new_id = cur.fetchone()[0]
        conn.commit()
        cur.close()
        conn.close()
        
        return {"id": new_id, "message": "Suggestion sent successfully"}
        
    except Exception as e:
        print(f"Error in send_suggestion: {e}")
        raise HTTPException(status_code=500, detail=str(e))

# 2. Получить все предложения для курсов текущего преподавателя (как создателя)
@app.get("/rest/v1/teacher/suggestions")
async def get_teacher_suggestions(teacher_id: int):
    try:
        conn = get_db_connection()
        cur = conn.cursor(cursor_factory=RealDictCursor)
        
        cur.execute("""
            SELECT 
                cs.id,
                cs.course_id,
                cs.title,
                cs.text_content,
                cs.video_url,
                cs.duration,
                cs.status,
                cs.created_at,
                u.first_name,
                u.last_name,
                u.country,
                c.title AS course_title
            FROM course_suggestions cs
            JOIN courses c ON cs.course_id = c.id
            JOIN users u ON cs.suggested_by = u.id
            WHERE c.creator_id = %s AND cs.status = 'pending'
            ORDER BY cs.created_at DESC
        """, (teacher_id,))
        
        suggestions = cur.fetchall()
        cur.close()
        conn.close()
        
        return suggestions
        
    except Exception as e:
        print(f"Error in get_teacher_suggestions: {e}")
        raise HTTPException(status_code=500, detail=str(e))

# 3. Одобрить предложение (создать урок в lessons)
@app.patch("/rest/v1/suggestions/{suggestion_id}/approve")
async def approve_suggestion(suggestion_id: int, teacher_id: int):
    try:
        conn = get_db_connection()
        cur = conn.cursor()
        
        # Получаем данные предложения
        cur.execute("""
            SELECT cs.course_id, cs.title, cs.text_content, cs.video_url, cs.duration, cs.suggested_by
            FROM course_suggestions cs
            JOIN courses c ON cs.course_id = c.id
            WHERE cs.id = %s AND c.creator_id = %s AND cs.status = 'pending'
        """, (suggestion_id, teacher_id))
        
        suggestion = cur.fetchone()
        if not suggestion:
            raise HTTPException(status_code=404, detail="Suggestion not found or already processed")
        
        course_id, title, text_content, video_url, duration, suggested_by = suggestion
        
        # Определяем порядок нового урока (добавляем в конец списка)
        cur.execute("SELECT COALESCE(MAX(\"order\"), 0) + 1 FROM lessons WHERE course_id = %s", (course_id,))
        new_order = cur.fetchone()[0]
        
        # Создаём реальный урок в таблице lessons
        cur.execute("""
            INSERT INTO lessons (course_id, title, description, video_url, text_content, "order", duration, is_brics, created_at)
            VALUES (%s, %s, %s, %s, %s, %s, %s, false, NOW())
            RETURNING id
        """, (course_id, title, f"Дополнительный урок от преподавателя", video_url, text_content, new_order, duration))
        
        new_lesson_id = cur.fetchone()[0]
        
        # Добавляем запись в course_contributions
        cur.execute("""
            INSERT INTO course_contributions (course_id, lesson_id, contributor_id, approved_by, approved_at)
            VALUES (%s, %s, %s, %s, NOW())
        """, (course_id, new_lesson_id, suggested_by, teacher_id))
        
        # Обновляем статус предложения
        cur.execute("""
            UPDATE course_suggestions 
            SET status = 'approved', updated_at = NOW() 
            WHERE id = %s
        """, (suggestion_id,))
        
        conn.commit()
        cur.close()
        conn.close()
        
        return {"success": True, "lesson_id": new_lesson_id, "message": "Lesson added to course"}
        
    except Exception as e:
        print(f"Error in approve_suggestion: {e}")
        raise HTTPException(status_code=500, detail=str(e))

# 4. Отклонить предложение
@app.patch("/rest/v1/suggestions/{suggestion_id}/reject")
async def reject_suggestion(suggestion_id: int, teacher_id: int, request: Request):
    try:
        body = await request.json()
        reason = body.get('reason', '')
        
        conn = get_db_connection()
        cur = conn.cursor()
        
        # Проверяем, что предложение принадлежит курсу создателя
        cur.execute("""
            UPDATE course_suggestions 
            SET status = 'rejected', rejected_reason = %s, updated_at = NOW()
            WHERE id = %s 
            AND course_id IN (SELECT id FROM courses WHERE creator_id = %s)
            AND status = 'pending'
            RETURNING id
        """, (reason, suggestion_id, teacher_id))
        
        updated = cur.fetchone()
        conn.commit()
        cur.close()
        conn.close()
        
        if not updated:
            raise HTTPException(status_code=404, detail="Suggestion not found or already processed")
        
        return {"success": True, "message": "Suggestion rejected"}
        
    except Exception as e:
        print(f"Error in reject_suggestion: {e}")
        raise HTTPException(status_code=500, detail=str(e))

# 5. Получить список дополнений для курса (одобренные уроки с информацией об авторе)
@app.get("/rest/v1/courses/{course_id}/contributions")
async def get_course_contributions(course_id: int):
    try:
        conn = get_db_connection()
        cur = conn.cursor(cursor_factory=RealDictCursor)
        
        cur.execute("""
            SELECT 
                l.*,
                u.first_name AS contributor_first_name,
                u.last_name AS contributor_last_name,
                u.country AS contributor_country,
                cc.approved_at
            FROM lessons l
            JOIN course_contributions cc ON l.id = cc.lesson_id
            JOIN users u ON cc.contributor_id = u.id
            WHERE l.course_id = %s
            ORDER BY l.order
        """, (course_id,))
        
        contributions = cur.fetchall()
        cur.close()
        conn.close()
        
        return contributions
        
    except Exception as e:
        print(f"Error in get_course_contributions: {e}")
        raise HTTPException(status_code=500, detail=str(e))

# 6. Получить список курсов преподавателя с количеством pending предложений (для подсветки)
@app.get("/rest/v1/teacher/courses_with_suggestions_count")
async def get_teacher_courses_with_suggestions_count(teacher_id: int):
    try:
        conn = get_db_connection()
        cur = conn.cursor(cursor_factory=RealDictCursor)
        
        cur.execute("""
            SELECT 
                c.id,
                c.title,
                COUNT(cs.id) AS pending_suggestions_count
            FROM courses c
            LEFT JOIN course_suggestions cs ON c.id = cs.course_id AND cs.status = 'pending'
            WHERE c.creator_id = %s
            GROUP BY c.id
            ORDER BY pending_suggestions_count DESC, c.created_at DESC
        """, (teacher_id,))
        
        result = cur.fetchall()
        cur.close()
        conn.close()
        
        return result
        
    except Exception as e:
        print(f"Error in get_teacher_courses_with_suggestions_count: {e}")
        raise HTTPException(status_code=500, detail=str(e))

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)









