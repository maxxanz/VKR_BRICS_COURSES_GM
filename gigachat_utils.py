import requests
import uuid
import json
import re
import tiktoken

AUTH_KEY = "MDE5ZTcwODYtODY1My03MzRkLTllNjEtMTFlYTI2YWZiZDZhOmU0M2YzZDQ1LWU4YTktNDE4Mi05MGRkLTA5OGY3ZjMyNWEzMA=="

def get_access_token():
    url = "https://ngw.devices.sberbank.ru:9443/api/v2/oauth"
    headers = {
        "Content-Type": "application/x-www-form-urlencoded",
        "Accept": "application/json",
        "RqUID": str(uuid.uuid4()),
        "Authorization": f"Basic {AUTH_KEY}"
    }
    data = {"scope": "GIGACHAT_API_PERS"}
    resp = requests.post(url, headers=headers, data=data, verify=False)
    resp.raise_for_status()
    return resp.json()["access_token"]

def count_tokens(text: str):
    enc = tiktoken.get_encoding("cl100k_base")
    return len(enc.encode(text))

def ask_gigachat(prompt, token=None, max_tokens=2000):
    if token is None:
        token = get_access_token()
    url = "https://gigachat.devices.sberbank.ru/api/v1/chat/completions"
    headers = {
        "Accept": "application/json",
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }
    payload = {
        "model": "GigaChat",
        "messages": [{"role": "user", "content": prompt}],
        "temperature": 0.3,
        "max_tokens": max_tokens
    }
    resp = requests.post(url, headers=headers, json=payload, verify=False)
    
    if resp.status_code == 400 and "max_prompt_tokens" in resp.text:
        print("⚠️ Превышен лимит промпта. Пробуем обрезать...")
        prompt = prompt[:6000]
        payload["messages"][0]["content"] = prompt
        resp = requests.post(url, headers=headers, json=payload, verify=False)
    
    resp.raise_for_status()
    return resp.json()["choices"][0]["message"]["content"]

def extract_full_from_syllabus(text, max_input_tokens=8000, max_retry=2):
    """
    Отправляет текст в GigaChat, при необходимости обрезает.
    При ошибке JSON повторяет с урезанным текстом.
    """
    # Удаляем нулевые байты (часто встречаются в PDF)
    text = text.replace('\x00', '')
    
    tokens_in = count_tokens(text)
    print(f"📊 Входной текст: {len(text)} символов, ~{tokens_in} токенов")
    
    if tokens_in > max_input_tokens:
        print(f"⚠️ Превышен лимит {max_input_tokens} токенов. Обрезаем...")
        max_chars = int(max_input_tokens * 2.5)  # примерная оценка
        text = text[:max_chars]
        print(f"✂️ Обрезано до {len(text)} символов (~{count_tokens(text)} токенов)")
    
    prompt = f"""
Ты — эксперт по образовательным стандартам (Россия ФГОС, Китай MOE, Индия AICTE). Проанализируй рабочую программу курса.

Извлеки:
1. Основные темы (главы, разделы) в порядке следования. Для каждой: order (число), title (короткое название), description (1-2 предложения).
2. Информацию о стандарте:
   - country: "RU", "CN", "IN" или "OTHER"
   - code: "ФГОС", "MOE", "AICTE" или другое
   - competencies: массив строк (для России) – коды компетенций, если есть
   - ideological_indicators: массив строк (для Китая) – например ["3.2", "5.1"]
   - accreditation: строка (для Индии) – например "AICTE"
   - total_hours: число (общее количество часов, если указано)
   - credits: число (кредиты/ЗЕТ)
   - assessment: "exam" или "credit" (если указано)

Верни ТОЛЬКО JSON без лишнего текста, строго по схеме:
{{
  "topics": [
    {{"order": 1, "title": "Введение", "description": "Обзор"}}
  ],
  "standard": {{
    "country": "RU",
    "code": "ФГОС",
    "competencies": ["УК-1", "ОПК-3"],
    "ideological_indicators": [],
    "accreditation": null,
    "total_hours": 144,
    "credits": 4,
    "assessment": "exam"
  }}
}}

Если данных нет, ставь null или пустой массив.

Текст программы:
{text}
"""
    
    for attempt in range(max_retry):
        raw = ask_gigachat(prompt, max_tokens=2000)
        json_match = re.search(r'\{.*\}', raw, re.DOTALL)
        if not json_match:
            print(f"⚠️ Попытка {attempt+1}: JSON не найден. Сырой ответ:\n{raw[:500]}")
            # Обрезаем текст ещё сильнее
            text = text[:4000]
            prompt = prompt.replace(text, text)  # обновляем
            continue
        try:
            data = json.loads(json_match.group())
            # Убедимся, что структура правильная
            if "topics" not in data:
                data["topics"] = []
            if "standard" not in data:
                data["standard"] = {}
            return data
        except json.JSONDecodeError as e:
            print(f"⚠️ Попытка {attempt+1}: ошибка декодирования JSON: {e}")
            print(f"Сырой ответ:\n{raw[:500]}")
            # Обрезаем текст
            text = text[:4000]
            prompt = prompt.replace(text, text)
    
    # Если все попытки провалились
    return {"topics": [], "standard": {}, "error": "Не удалось получить валидный JSON после нескольких попыток"}
