--
-- PostgreSQL database dump
--

\restrict UuexPRi2FvvrkPW0fg2Cge1iIZzNxofJOHFKZdqwMcjHwHBCFx2wkyBvaKKazID

-- Dumped from database version 16.14 (Ubuntu 16.14-0ubuntu0.24.04.1)
-- Dumped by pg_dump version 16.13

-- Started on 2026-06-10 17:36:36

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 229 (class 1259 OID 16520)
-- Name: user_test_results; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.user_test_results (
    id integer NOT NULL,
    user_id integer,
    test_id integer,
    score integer,
    total_questions integer,
    percentage integer,
    passed boolean,
    completed_at timestamp without time zone DEFAULT now()
);


ALTER TABLE public.user_test_results OWNER TO postgres;

--
-- TOC entry 216 (class 1259 OID 16409)
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    id integer NOT NULL,
    email character varying(255) NOT NULL,
    password character varying(255) NOT NULL,
    first_name character varying(100),
    last_name character varying(100),
    country character varying(100),
    user_type character varying(50),
    university character varying(255),
    created_at timestamp without time zone DEFAULT now()
);


ALTER TABLE public.users OWNER TO postgres;

--
-- TOC entry 232 (class 1259 OID 16567)
-- Name: country_ranking; Type: VIEW; Schema: public; Owner: app_user
--

CREATE VIEW public.country_ranking AS
 SELECT u.country,
    round(avg(utr.percentage), 1) AS avg_score,
    count(DISTINCT u.id) AS total_users,
    count(utr.id) AS total_tests,
    row_number() OVER (ORDER BY (avg(utr.percentage)) DESC) AS rank
   FROM (public.users u
     JOIN public.user_test_results utr ON ((u.id = utr.user_id)))
  WHERE ((u.country IS NOT NULL) AND ((u.country)::text <> ''::text) AND (utr.passed = true))
  GROUP BY u.country;


ALTER VIEW public.country_ranking OWNER TO app_user;

--
-- TOC entry 234 (class 1259 OID 16579)
-- Name: course_contributions; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.course_contributions (
    id integer NOT NULL,
    course_id integer NOT NULL,
    lesson_id integer NOT NULL,
    contributor_id integer NOT NULL,
    approved_by integer NOT NULL,
    approved_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.course_contributions OWNER TO postgres;

--
-- TOC entry 233 (class 1259 OID 16578)
-- Name: course_contributions_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.course_contributions_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.course_contributions_id_seq OWNER TO postgres;

--
-- TOC entry 3569 (class 0 OID 0)
-- Dependencies: 233
-- Name: course_contributions_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.course_contributions_id_seq OWNED BY public.course_contributions.id;


--
-- TOC entry 236 (class 1259 OID 16609)
-- Name: course_suggestions; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.course_suggestions (
    id integer NOT NULL,
    course_id integer NOT NULL,
    suggested_by integer NOT NULL,
    title character varying(255) NOT NULL,
    text_content text,
    video_url character varying(500),
    duration character varying(50),
    status character varying(20) DEFAULT 'pending'::character varying,
    rejected_reason text,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.course_suggestions OWNER TO postgres;

--
-- TOC entry 235 (class 1259 OID 16608)
-- Name: course_suggestions_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.course_suggestions_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.course_suggestions_id_seq OWNER TO postgres;

--
-- TOC entry 3572 (class 0 OID 0)
-- Dependencies: 235
-- Name: course_suggestions_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.course_suggestions_id_seq OWNED BY public.course_suggestions.id;


--
-- TOC entry 225 (class 1259 OID 16486)
-- Name: course_tests; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.course_tests (
    id integer NOT NULL,
    course_id integer,
    title character varying(200) DEFAULT 'Финальный тест'::character varying,
    passing_score integer DEFAULT 70,
    created_at timestamp without time zone DEFAULT now()
);


ALTER TABLE public.course_tests OWNER TO postgres;

--
-- TOC entry 224 (class 1259 OID 16485)
-- Name: course_tests_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.course_tests_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.course_tests_id_seq OWNER TO postgres;

--
-- TOC entry 3575 (class 0 OID 0)
-- Dependencies: 224
-- Name: course_tests_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.course_tests_id_seq OWNED BY public.course_tests.id;


--
-- TOC entry 218 (class 1259 OID 16421)
-- Name: courses; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.courses (
    id integer NOT NULL,
    title character varying(200) NOT NULL,
    subject character varying(100),
    description text,
    image_url text,
    course_number integer,
    creator_id integer,
    duration integer,
    rating numeric(3,2) DEFAULT 0,
    students_count integer DEFAULT 0,
    created_at timestamp without time zone DEFAULT now(),
    image_data bytea,
    profile jsonb
);


ALTER TABLE public.courses OWNER TO postgres;

--
-- TOC entry 217 (class 1259 OID 16420)
-- Name: courses_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.courses_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.courses_id_seq OWNER TO postgres;

--
-- TOC entry 3578 (class 0 OID 0)
-- Dependencies: 217
-- Name: courses_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.courses_id_seq OWNED BY public.courses.id;


--
-- TOC entry 219 (class 1259 OID 16437)
-- Name: courses_with_creators; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.courses_with_creators AS
 SELECT c.id,
    c.title,
    c.subject,
    c.description,
    c.image_url,
    c.course_number,
    c.creator_id,
    c.duration,
    c.rating,
    c.students_count,
    c.created_at,
    u.first_name AS creator_first_name,
    u.last_name AS creator_last_name,
    u.country AS creator_country,
    u.university AS creator_university,
    u.user_type AS creator_type
   FROM (public.courses c
     LEFT JOIN public.users u ON ((c.creator_id = u.id)));


ALTER VIEW public.courses_with_creators OWNER TO postgres;

--
-- TOC entry 223 (class 1259 OID 16470)
-- Name: lessons; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.lessons (
    id integer NOT NULL,
    course_id integer,
    title character varying(200) NOT NULL,
    description text,
    video_url text,
    text_content text,
    "order" integer DEFAULT 0,
    duration integer,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now(),
    is_brics boolean DEFAULT false
);


ALTER TABLE public.lessons OWNER TO postgres;

--
-- TOC entry 222 (class 1259 OID 16469)
-- Name: lessons_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.lessons_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.lessons_id_seq OWNER TO postgres;

--
-- TOC entry 3582 (class 0 OID 0)
-- Dependencies: 222
-- Name: lessons_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.lessons_id_seq OWNED BY public.lessons.id;


--
-- TOC entry 227 (class 1259 OID 16503)
-- Name: test_questions; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.test_questions (
    id integer NOT NULL,
    test_id integer,
    question_text text NOT NULL,
    option_a character varying(500),
    option_b character varying(500),
    option_c character varying(500),
    option_d character varying(500),
    correct_answer character(1),
    points integer DEFAULT 1,
    "order" integer DEFAULT 0,
    CONSTRAINT test_questions_correct_answer_check CHECK ((correct_answer = ANY (ARRAY['A'::bpchar, 'B'::bpchar, 'C'::bpchar, 'D'::bpchar])))
);


ALTER TABLE public.test_questions OWNER TO postgres;

--
-- TOC entry 226 (class 1259 OID 16502)
-- Name: test_questions_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.test_questions_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.test_questions_id_seq OWNER TO postgres;

--
-- TOC entry 3585 (class 0 OID 0)
-- Dependencies: 226
-- Name: test_questions_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.test_questions_id_seq OWNED BY public.test_questions.id;


--
-- TOC entry 221 (class 1259 OID 16443)
-- Name: user_courses; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.user_courses (
    id integer NOT NULL,
    user_id integer NOT NULL,
    course_id integer NOT NULL,
    status character varying(20) DEFAULT 'saved'::character varying,
    result numeric(5,2) DEFAULT NULL::numeric,
    started_at timestamp without time zone,
    completed_at timestamp without time zone,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now(),
    CONSTRAINT user_courses_status_check CHECK (((status)::text = ANY ((ARRAY['saved'::character varying, 'in_progress'::character varying, 'completed'::character varying])::text[])))
);


ALTER TABLE public.user_courses OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 16442)
-- Name: user_courses_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.user_courses_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.user_courses_id_seq OWNER TO postgres;

--
-- TOC entry 3588 (class 0 OID 0)
-- Dependencies: 220
-- Name: user_courses_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.user_courses_id_seq OWNED BY public.user_courses.id;


--
-- TOC entry 231 (class 1259 OID 16540)
-- Name: user_lesson_progress; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.user_lesson_progress (
    id integer NOT NULL,
    user_id integer NOT NULL,
    lesson_id integer NOT NULL,
    is_completed boolean DEFAULT false,
    completed_at timestamp without time zone,
    score integer,
    updated_at timestamp without time zone DEFAULT now()
);


ALTER TABLE public.user_lesson_progress OWNER TO postgres;

--
-- TOC entry 230 (class 1259 OID 16539)
-- Name: user_lesson_progress_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.user_lesson_progress_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.user_lesson_progress_id_seq OWNER TO postgres;

--
-- TOC entry 3591 (class 0 OID 0)
-- Dependencies: 230
-- Name: user_lesson_progress_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.user_lesson_progress_id_seq OWNED BY public.user_lesson_progress.id;


--
-- TOC entry 228 (class 1259 OID 16519)
-- Name: user_test_results_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.user_test_results_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.user_test_results_id_seq OWNER TO postgres;

--
-- TOC entry 3593 (class 0 OID 0)
-- Dependencies: 228
-- Name: user_test_results_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.user_test_results_id_seq OWNED BY public.user_test_results.id;


--
-- TOC entry 215 (class 1259 OID 16408)
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.users_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.users_id_seq OWNER TO postgres;

--
-- TOC entry 3595 (class 0 OID 0)
-- Dependencies: 215
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- TOC entry 3331 (class 2604 OID 16582)
-- Name: course_contributions id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.course_contributions ALTER COLUMN id SET DEFAULT nextval('public.course_contributions_id_seq'::regclass);


--
-- TOC entry 3333 (class 2604 OID 16612)
-- Name: course_suggestions id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.course_suggestions ALTER COLUMN id SET DEFAULT nextval('public.course_suggestions_id_seq'::regclass);


--
-- TOC entry 3319 (class 2604 OID 16489)
-- Name: course_tests id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.course_tests ALTER COLUMN id SET DEFAULT nextval('public.course_tests_id_seq'::regclass);


--
-- TOC entry 3305 (class 2604 OID 16424)
-- Name: courses id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.courses ALTER COLUMN id SET DEFAULT nextval('public.courses_id_seq'::regclass);


--
-- TOC entry 3314 (class 2604 OID 16473)
-- Name: lessons id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.lessons ALTER COLUMN id SET DEFAULT nextval('public.lessons_id_seq'::regclass);


--
-- TOC entry 3323 (class 2604 OID 16506)
-- Name: test_questions id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.test_questions ALTER COLUMN id SET DEFAULT nextval('public.test_questions_id_seq'::regclass);


--
-- TOC entry 3309 (class 2604 OID 16446)
-- Name: user_courses id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_courses ALTER COLUMN id SET DEFAULT nextval('public.user_courses_id_seq'::regclass);


--
-- TOC entry 3328 (class 2604 OID 16543)
-- Name: user_lesson_progress id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_lesson_progress ALTER COLUMN id SET DEFAULT nextval('public.user_lesson_progress_id_seq'::regclass);


--
-- TOC entry 3326 (class 2604 OID 16523)
-- Name: user_test_results id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_test_results ALTER COLUMN id SET DEFAULT nextval('public.user_test_results_id_seq'::regclass);


--
-- TOC entry 3303 (class 2604 OID 16412)
-- Name: users id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- TOC entry 3557 (class 0 OID 16579)
-- Dependencies: 234
-- Data for Name: course_contributions; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.course_contributions (id, course_id, lesson_id, contributor_id, approved_by, approved_at) FROM stdin;
2	1	132	24	1	2026-05-28 16:32:34.808566
3	38	134	24	34	2026-05-28 16:43:11.26883
4	1	135	34	1	2026-05-28 17:37:10.800794
5	23	136	1	5	2026-05-28 17:49:42.080741
\.


--
-- TOC entry 3559 (class 0 OID 16609)
-- Dependencies: 236
-- Data for Name: course_suggestions; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.course_suggestions (id, course_id, suggested_by, title, text_content, video_url, duration, status, rejected_reason, created_at, updated_at) FROM stdin;
1	1	24	1212	12	12	12	rejected	21212	2026-05-28 16:10:02.740375	2026-05-28 16:12:38.642443
2	1	24	12	12	211	2	approved	\N	2026-05-28 16:13:49.355325	2026-05-28 16:14:13.653987
3	1	24	1111	fdgsdfdfd	23	3	approved	\N	2026-05-28 16:31:59.386063	2026-05-28 16:32:34.808566
4	38	24	Интересные факты	...	...	20	approved	\N	2026-05-28 16:40:09.476436	2026-05-28 16:43:11.26883
5	38	24	ваа	ваав	ва	21	rejected	Не соответствует теме курса	2026-05-28 16:41:14.497149	2026-05-28 16:44:08.297218
7	38	1	444	4444\n55\n55\n66\n66	44	4	pending	\N	2026-05-28 17:28:47.582787	2026-05-28 17:28:47.582787
8	23	1	Искусственный интеллект в экономике	Искусственный интеллект (ИИ) и его роль в цифровой экономике\r\nИскусственный интеллект — это способность компьютеров выполнять задачи, требующие человеческого интеллекта: распознавание образов, принятие решений, обработка естественного языка.\r\n\r\nГде применяется ИИ сегодня:\r\n• Финансовый сектор — скоринг клиентов, обнаружение мошенничества\r\n• Ритейл — рекомендательные системы (Ozon, Wildberries)\r\n• Логистика — оптимизация маршрутов доставки\r\n• Медицина — диагностика по снимкам\r\n\r\nПримеры в России:\r\n• Сбер — голосовой ассистент, система распознавания лиц\r\n• Яндекс — алгоритмы поиска, беспилотные автомобили\r\n• Тинькофф — чат-бот, кредитный скоринг на основе ИИ\r\n\r\nВызовы и риски:\r\n• Этические вопросы (предвзятость алгоритмов)\r\n• Безопасность данных\r\n• Регулирование (закон об ИИ в РФ)\r\n\r\nКлючевой вывод: ИИ становится ключевым фактором конкурентоспособности компаний в цифровой экономике.	1	10	pending	\N	2026-05-28 17:48:24.101167	2026-05-28 17:49:42.080741
6	23	34	Блокчейн и криптовалюты	Что такое блокчейн?\r\nБлокчейн — это распределённая база данных, где информация хранится в виде цепочки блоков. Каждый блок содержит данные и связан с предыдущим с помощью криптографии.\r\n\r\nОсновные свойства блокчейна:\r\n• Децентрализация — нет единого сервера\r\n• Неизменяемость данных — нельзя изменить прошлые записи\r\n• Прозрачность — все транзакции видны участникам\r\n\r\nКриптовалюты (Биткоин, Эфириум) — это цифровые деньги, работающие на блокчейне. Они позволяют совершать переводы без посредников (банков).\r\n\r\nПрименение в России:\r\n• Тестирование цифрового рубля (Банк России)\r\n• Учёт товаров в цепочках поставок\r\n• Системы голосования\r\n\r\nКлючевой вывод: блокчейн — это не только криптовалюты, но и технология для создания доверия в цифровом мире.	444	10	pending	\N	2026-05-28 17:27:30.294746	2026-05-28 17:37:10.800794
\.


--
-- TOC entry 3549 (class 0 OID 16486)
-- Dependencies: 225
-- Data for Name: course_tests; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.course_tests (id, course_id, title, passing_score, created_at) FROM stdin;
1	1	Финальный тест: Основы цифровой экономики	50	2026-04-29 17:42:11.274997
2	18	Финальный тест: Математический анализ	50	2026-04-29 17:43:18.941444
3	22	Финальный тест: Искусственный интеллект и нейросети	50	2026-05-11 20:14:04.488748
4	23	Финальный тест: Большие данные и аналитика	50	2026-05-11 20:14:04.547834
18	37	Финальный тест	70	2026-05-12 20:45:08.889544
19	38	Финальный тест	70	2026-05-12 22:42:09.957808
24	60	Финальный тест	70	2026-05-25 12:09:54.373211
25	61	Финальный тест	70	2026-05-25 13:16:32.033135
26	64	Финальный тест	70	2026-05-29 07:00:02.468093
27	67	Финальный тест	70	2026-05-29 12:01:31.933036
28	68	Финальный тест	70	2026-05-31 16:39:31.961498
29	70	Финальный тест	70	2026-06-01 18:16:08.46963
30	71	Финальный тест	70	2026-06-01 18:54:23.5965
31	72	Финальный тест	70	2026-06-03 11:57:54.093803
\.


--
-- TOC entry 3543 (class 0 OID 16421)
-- Dependencies: 218
-- Data for Name: courses; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.courses (id, title, subject, description, image_url, course_number, creator_id, duration, rating, students_count, created_at, image_data, profile) FROM stdin;
2	Культура стран БРИКС	Культурология	Изучение культурных особенностей, традиций и обычаев стран БРИКС.	https://images.unsplash.com/photo-1524178232363-1fb2b075b655?w=600	1	2	300	4.70	2150	2026-04-28 21:16:28.878168	\N	\N
3	Языки стран БРИКС	Лингвистика	Основы португальского, китайского, хинди и других языков стран БРИКС.	https://images.unsplash.com/photo-1456513080510-7bf3a84b82f8?w=600	1	3	480	4.50	3100	2026-04-28 21:16:28.878168	\N	\N
5	Введение в программирование	Информационные технологии	Основы программирования: алгоритмы, структуры данных, первый язык программирования.	https://images.unsplash.com/photo-1587620962725-abab7fe55159?w=600	2	1	480	4.60	890	2026-04-28 21:16:28.878168	\N	\N
8	Цифровые технологии	Информационные технологии	Современные IT решения: облачные технологии, искусственный интеллект, машинное обучение.	https://images.unsplash.com/photo-1518770660439-4636190af475?w=600	3	1	480	4.80	3420	2026-04-28 21:16:28.878168	\N	\N
1	Основы цифровой экономики	Экономика	Изучите базовые принципы цифровой экономики, цифровые платформы и их влияние на современный бизнес.	https://images.unsplash.com/photo-1581091226033-d5c48150dbaa?w=600	1	1	360	4.80	1250	2026-04-28 21:16:28.878168	\N	\N
6	Основы маркетинга	Маркетинг	Базовые принципы маркетинга, исследование рынка, целевая аудитория.	https://images.unsplash.com/photo-1557838923-2985c318be48?w=600	2	2	360	4.70	720	2026-04-28 21:16:28.878168	\N	\N
7	Международная торговля	Экономика	Современные подходы к международной торговле, таможенное регулирование, логистика.	https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=600	3	3	420	4.90	980	2026-04-28 21:16:28.878168	\N	\N
17	Глобальная экономика	Экономика	Анализ глобальных экономических процессов и их влияние на страны БРИКС.	https://api-reforum.banki.ru/reforum/96/2a/8a/2f/7f/t8jz8v3b74e83132.jpeg	6	4	480	4.80	180	2026-04-28 21:16:28.878168	\N	\N
10	Психология лидерства	Психология	Навыки эффективного лидерства: управление командой, эмоциональный интеллект.	https://images.unsplash.com/photo-1522071820081-009f0129c71c?w=600	3	4	390	4.80	720	2026-04-28 21:16:28.878168	\N	\N
11	Экономическое сотрудничество	Экономика	Принципы экономического партнерства между странами БРИКС, совместные проекты.	https://images.unsplash.com/photo-1526304640581-d334cdbbf45e?w=600	4	1	540	4.60	890	2026-04-28 21:16:28.878168	\N	\N
12	Компьютерная графика	Информационные технологии	Программирование визуальных изображений: 3D-моделирование, визуализация данных.	https://images.unsplash.com/photo-1545671913-b89ac1b4ac10?w=600	4	2	420	3.00	320	2026-04-28 21:16:28.878168	\N	\N
13	Экология и устойчивое развитие	Экология	Проблемы экологии и пути устойчивого развития стран БРИКС.	https://images.unsplash.com/photo-1542601906990-b4d3fb778b09?w=600	4	3	330	4.70	430	2026-04-28 21:16:28.878168	\N	\N
14	Финансовые технологии (FinTech)	Финансы	Обзор финансовых технологий, блокчейн, криптовалюты, цифровые платежи.	https://images.unsplash.com/photo-1518186285589-2f7649de83e0?w=600	5	1	600	4.80	520	2026-04-28 21:16:28.878168	\N	\N
15	Искусственный интеллект	Информационные технологии	Продвинутый курс по машинному обучению, нейронным сетям и AI.	https://images.unsplash.com/photo-1677442136019-21780ecad995?w=600	5	2	720	4.90	340	2026-04-28 21:16:28.878168	\N	\N
16	Стратегическое управление	Бизнес	Методы стратегического планирования и управления крупными проектами.	https://images.unsplash.com/photo-1552664730-d307ca884978?w=600	6	3	540	4.90	210	2026-04-28 21:16:28.878168	\N	\N
18	Математический анализ для начинающих	Математика	Курс охватывает основные понятия математического анализа: пределы, производные, интегралы и их применение. Подходит для студентов первых курсов технических специальностей.	https://images.unsplash.com/photo-1509228468518-180dd4864904?w=600	1	2	480	4.90	450	2026-04-29 17:39:39.199505	\N	\N
4	Философия для начинающих	Философия	Введение в философию: основные концепции, мыслители и идеи для повседневной жизни.	https://images.unsplash.com/photo-1506126613408-eca07ce68773?w=600	1	4	360	4.90	560	2026-04-28 21:16:28.878168	\N	\N
22	Искусственный интеллект и нейросети	AI	Продвинутый курс по нейронным сетям	https://www.hse.ru/data/2024/05/23/2126095409/1686de8d-4511-4ea4-abd1-8e215b20cc2f.jpg.(1000x1000x1).jpg	2	1	600	4.90	0	2026-05-11 19:59:41.699722	\N	\N
23	Большие данные и аналитика	Data Science	Обработка больших данных	https://i.pinimg.com/originals/ae/45/c9/ae45c94661fc6f5adb2b9b66c9feb93a.jpg	3	5	540	4.80	0	2026-05-11 19:59:41.699722	\N	\N
24	БРИКС: экономика и политика	Economics	Анализ экономик стран БРИКС	https://avatars.mds.yandex.net/i?id=0a7fb86f021b0a10004b1e42aad3db58_l-5360359-images-thumbs&n=13	2	3	480	4.70	0	2026-05-11 19:59:41.699722	\N	\N
9	Математический анализ	Математика	Основы математического анализа: пределы, производные, интегралы.	http://avatars.mds.yandex.net/get-vthumb/3072802/ef7d0b18acee5fcafb9008898055e849/800x450	3	2	600	5.00	450	2026-04-28 21:16:28.878168	\N	\N
37	Мобильные приложения	Программировнаие	Изучение разработки приложений на мобильные устройства	https://media.proglib.io/wp-uploads/2019/02/functional-programming-logo.jpg	3	24	30	0.00	0	2026-05-12 20:39:57.370608	\N	\N
38	Язык программирования c#	Программирование	Изучение языка программирования c#	https://static.cnews.ru/img/book/2024/04/16/c_sharp_logo_2023.png	2	34	150	0.00	0	2026-05-12 22:41:13.560416	\N	\N
45	Тестовый курс	Программирование	Курс для тестирования импорта		100	1	120	0.00	0	2026-05-21 14:33:18.956528	\N	\N
46	Тестовый курс2	Программирование2	Курс для тестирования импорта2		100	1	120	0.00	0	2026-05-21 14:42:43.125978	\N	\N
47	11	12	1		1	24	1	0.00	0	2026-05-21 14:50:49.828301	\N	\N
48	Большие данные	Программирование	1111		5	24	120	0.00	0	2026-05-21 19:26:04.994281	\N	\N
60	Базы данных	Программирование	Введение в основы базы данных	https://avatars.mds.yandex.net/i?id=4dc40a180022e5a0c33b6b6895527b18_l-5304692-images-thumbs&n=13	3	24	300	0.00	0	2026-05-25 12:04:32.562289	\N	\N
61	Основы программирования на языке C	Программирование	Введение в основы языка C, начальный курс программирования, где студенты изучают базовые конструкции языка: переменные, условия, циклы, функции и массивы.	https://i.ytimg.com/vi/zJQ1q4D0K8k/maxresdefault.jpg	1	24	200	0.00	0	2026-05-25 12:58:39.093543	\N	\N
62	1	1	1	1	1	24	1	0.00	0	2026-05-25 17:21:53.866676	\N	{"code": "MOE", "country": "CN", "credits": null, "assessment": "exam", "total_hours": null, "competencies": [], "accreditation": null, "ideological_indicators": ["3.2", "5.1", "8.1", "10.2"]}
63	2	2	2	2	2	24	10	4.30	0	2026-05-29 06:50:01.089057	\N	{"code": "ФГОС", "country": "RU", "credits": 4, "assessment": "exam", "total_hours": 144, "competencies": ["УК-1", "УК-2"], "accreditation": null, "ideological_indicators": []}
64	3	3	3	3	3	27	3	0.00	0	2026-05-29 06:54:13.71674	\N	{"code": "MOE", "country": "CN", "credits": null, "assessment": "exam", "total_hours": null, "competencies": [], "accreditation": null, "ideological_indicators": ["3.2", "5.1", "8.1", "10.2"]}
65	Анализ больших данных	Большие данные	Введение и основные понятия предмета Анализ больших данных	https://thb.tildacdn.com/tild3666-3262-4939-b763-616330353661/-/resize/504x/BigData2-768x339.jpg	4	27	300	0.00	0	2026-05-29 07:05:29.549914	\N	\N
66	Анализ больших данных	Большие данные	Введение и основные понятия предмета Анализ больших данных	https://thb.tildacdn.com/tild3666-3262-4939-b763-616330353661/-/resize/504x/BigData2-768x339.jpg	4	27	300	0.00	0	2026-05-29 07:07:23.249797	\N	{"code": "MOE", "country": "CN", "credits": null, "assessment": "exam", "total_hours": null, "competencies": [], "accreditation": null, "ideological_indicators": ["3.2", "5.1", "8.1", "10.2"]}
67	новый курс с телефона	математика	...	https://avatars.mds.yandex.net/i?id=c5f60599365c9183474c8252f14db7ac_l-5653325-images-thumbs&n=33&w=1080&h=1080	1	34	10	0.00	0	2026-05-29 11:58:47.21084	\N	{"code": "MOE", "country": "CN", "credits": null, "assessment": "exam", "total_hours": null, "competencies": [], "accreditation": null, "ideological_indicators": ["3.2", "5.1", "8.1", "10.2"]}
68	е	е	е	1	1	34	2	0.00	0	2026-05-31 16:38:38.572149	\N	{"code": "MOE", "country": "CN", "credits": null, "assessment": null, "total_hours": null, "competencies": [], "accreditation": null, "ideological_indicators": ["3.2", "5.1", "8.1", "10.2"]}
69	2	2	12	21	21	34	212	0.00	0	2026-06-01 17:46:01.295937	\N	\N
70	Введение в программирование	Программирование	12	21	21	34	212	0.00	0	2026-06-01 17:46:20.546493	\N	\N
71	Анализ больших данных	Большие данные	122	123	2	34	100	0.00	0	2026-06-01 18:16:54.170706	\N	{"code": "MOE", "country": "CN", "credits": null, "assessment": "exam", "total_hours": null, "competencies": [], "accreditation": null, "ideological_indicators": ["3.2", "5.1", "8.1", "10.2"]}
72	г	г	татк	1	1	34	1	0.00	0	2026-06-03 11:56:55.570712	\N	{"code": "MOE", "country": "CN", "credits": null, "assessment": "exam", "total_hours": null, "competencies": [], "accreditation": null, "ideological_indicators": ["3.2", "5.1", "8.1", "10.2"]}
\.


--
-- TOC entry 3547 (class 0 OID 16470)
-- Dependencies: 223
-- Data for Name: lessons; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.lessons (id, course_id, title, description, video_url, text_content, "order", duration, created_at, updated_at, is_brics) FROM stdin;
3	1	Бизнес-модели в цифровой экономике	Типы монетизации и бизнес-модели IT-компаний	https://www.youtube.com/embed/dQw4w9WgXcQ	Основные бизнес-модели\r\n\r\n• Freemium\r\n• Подписка (SaaS)\r\n• Транзакционные комиссии\r\n• Рекламная модель\r\n• Модель данных\r\n	3	18	2026-04-29 17:40:50.031602	2026-05-22 13:56:45.512566	f
13	22	Нейронные сети	Основы работы нейронных сетей		Перцептрон\r\nНейронные сети вдохновлены работой мозга человека. Перцептрон - это простейшая нейронная сеть, состоящая из одного нейрона.\r\n	3	30	2026-05-11 19:51:49.591887	2026-05-22 13:56:45.512566	f
2	1	Цифровые платформы и экосистемы	Как работают современные цифровые платформы	https://www.youtube.com/embed/dQw4w9WgXcQ	Платформенная экономика\r\n\r\nПлатформы соединяют поставщиков и потребителей. Примеры: Ozon, Wildberries, Avito, Яндекс.Такси.\r\n\r\nПреимущества платформ: снижение транзакционных издержек, масштабируемость, сетевые эффекты.	2	20	2026-04-29 17:40:50.031602	2026-05-22 13:56:45.512566	f
121	60	Урок 3: Связи между таблицами (Ключи)	\N		Цель: Понять, как хранить данные без повторов.\n\nТеория: Данные делят на отдельные таблицы, а соединяют их ключами.\n\nПервичный ключ (PK): Уникальный ID каждой записи (например, ID_книги).\n\nВнешний ключ (FK): Ссылка на чужой ID (например, ID_автора в таблице «Книги»).\n\nТипы связей: 1 ко многим (один автор — много книг).\n\nПрактика: Нарисуйте схему: таблица «Покупатели» и таблица «Заказы». Как связать, чтобы у заказа был владелец?	3	20	2026-05-25 12:08:15.306178	2026-05-25 12:08:15.306178	f
5	1	Кейсы и практические примеры	Разбор реальных примеров цифровой трансформации бизнеса	https://www.youtube.com/embed/dQw4w9WgXcQ	Примеры успешной цифровой трансформации\r\n\r\nСбер, Тинькофф, Яндекс. Кейсы внедрения цифровых решений в традиционных компаниях.\r\n\r\nУроки и выводы.	5	25	2026-04-29 17:40:50.031602	2026-05-22 13:56:45.512566	f
1	1	Введение в цифровую экономику	Что такое цифровая экономика, основные понятия и термины	https://vkvideo.ru/video-68049176_456240802?list=ln-bDrVh2M7Hrt02ZzFBX	Что такое цифровая экономика?\r\n\r\nЦифровая экономика — это экономическая деятельность, основанная на цифровых технологиях. Включает электронную коммерцию, онлайн-сервисы, платформенные решения.\r\n\r\nКлючевые характеристики:\r\n- Ключевые характеристики\r\n- Примеры платформ\r\n- Тренды развития'	1	15	2026-04-29 17:40:50.031602	2026-05-22 13:56:45.512566	f
6	18	Введение в математический анализ	Основные понятия: функция, предел, непрерывность	https://www.youtube.com/embed/dQw4w9WgXcQ	Что изучает математический анализ?\r\n\r\nМатематический анализ — раздел математики, изучающий функции, их пределы, производные и интегралы.\r\n\r\nКлючевые понятия:\r\n• Функция и её свойства\r\n• Предел функции\r\n• Непрерывность функции\r\n\r\nФункция f(x) называется непрерывной в точке x₀, если lim_{x→x₀} f(x) = f(x₀).	1	20	2026-04-29 17:41:43.179862	2026-05-22 13:56:45.512566	f
7	18	Пределы и их свойства	Вычисление пределов, замечательные пределы	https://www.youtube.com/embed/dQw4w9WgXcQ	Основные теоремы о пределах\r\n\r\n• Предел суммы = сумме пределов\r\n• Предел произведения = произведению пределов\r\n• Предел частного = частному пределов (знаменатель ≠ 0)\r\n\r\nПервый замечательный предел:\r\nlim_{x→0} sin(x)/x = 1\r\n\r\nВторой замечательный предел:\r\nlim_{x→∞} (1 + 1/x)^x = e	2	30	2026-04-29 17:41:43.179862	2026-05-22 13:56:45.512566	f
8	18	Производная функции	Определение производной, правила дифференцирования	https://www.youtube.com/embed/dQw4w9WgXcQ	Производная — скорость изменения функции\r\n\r\nf'(x₀) = lim_{Δx→0} (f(x₀+Δx) - f(x₀))/Δx\r\n\r\nТаблица производных:\r\n• (xⁿ)'' = n·xⁿ⁻¹\r\n• (sin x)'' = cos x\r\n• (cos x)'' = -sin x\r\n• (eˣ)'' = eˣ\r\n• (ln x)'' = 1/x\r\n\r\nПравила дифференцирования:\r\n• (u+v)'' = u'' + v''\r\n• (u·v)'' = u''·v + u·v''\r\n• (u/v)'' = (u''·v - u·v'')/v²\r\n\r\n	3	35	2026-04-29 17:41:43.179862	2026-05-22 13:56:45.512566	f
9	18	Применение производной	Исследование функций, экстремумы, оптимизация	https://www.youtube.com/embed/dQw4w9WgXcQ	Геометрический смысл производной\r\n\r\nПроизводная — угловой коэффициент касательной к графику функции.\r\n\r\nИсследование функции:\r\n• Точки экстремума (f''(x)=0)\r\n• Интервалы возрастания/убывания\r\n• Выпуклость и точки перегиба\r\n\r\nПрименения:\r\n• Задачи на максимум/минимум\r\n• Оптимизация производственных процессов\r\n• Физика: скорость и ускорение	4	28	2026-04-29 17:41:43.179862	2026-05-22 13:56:45.512566	f
136	23	11	Дополнительный урок от преподавателя	1	44\nfdsdff\n55\n\n5t\nfdgdfg\n\ngfgdg	3	1	2026-05-28 17:49:42.080741	2026-05-28 17:49:42.080741	f
137	1	Введение в эволюцию данных и программного обеспечения	Основные понятия, коэволюция данных и программного обеспечения, структуры данных.	\N	# Введение в эволюцию данных и программного обеспечения\n\nОсновные понятия, коэволюция данных и программного обеспечения, структуры данных.	1	60	2026-05-29 04:55:19.127568	2026-05-29 04:55:19.127568	f
158	64	🌍 Южная Африка. Применение курса.	\N	231	12231	7	23	2026-05-29 07:00:00.962928	2026-05-29 07:00:00.962928	t
138	1	Представление данных в вычислительных системах	Системы счисления, типы данных, иерархия памяти.	\N	# Представление данных в вычислительных системах\n\nСистемы счисления, типы данных, иерархия памяти.	2	60	2026-05-29 04:55:19.127568	2026-05-29 04:55:19.127568	f
139	1	Линейные структуры данных	Массивы, связанные списки, множества, карты, временная сложность.	\N	# Линейные структуры данных\n\nМассивы, связанные списки, множества, карты, временная сложность.	3	60	2026-05-29 04:55:19.127568	2026-05-29 04:55:19.127568	f
25	37	Основы интерфейса	\N	https://vkvideo.ru/video-85562117_456239055	Активность (Activity) — это экран приложения. Фрагмент (Fragment) — часть экрана, которую можно переиспользовать. Все элементы управления (кнопки, текст, изображения) описываются в XML-разметке.	1	10	2026-05-12 20:42:35.754762	2026-05-22 13:56:45.512566	f
26	37	Работа с сетью	\N	https://vkvideo.ru/video-220605847_456239075	Для отправки запросов на сервер используется библиотека Retrofit. Она превращает ответы сервера в Java-объекты. Важно всегда обрабатывать ошибки сети в колбэках onFailure и onResponse.	2	15	2026-05-12 20:43:23.922728	2026-05-22 13:56:45.512566	f
27	37	Хранение данных	\N	https://vkvideo.ru/video-111905078_456244421	SharedPreferences подходит для хранения настроек и ID пользователя. Для сложных данных используйте базу данных SQLite через библиотеку Room.	3	20	2026-05-12 20:44:58.749033	2026-05-22 13:56:45.512566	f
28	38	Вводный	\N		Что такое с# смотрите в видеоролике	1	20	2026-05-12 22:42:00.806002	2026-05-22 13:56:45.512566	f
140	1	Иерархические структуры данных	Деревья, бинарные деревья, бинарные деревья поиска, кучи, алгоритмы обхода.	\N	# Иерархические структуры данных\n\nДеревья, бинарные деревья, бинарные деревья поиска, кучи, алгоритмы обхода.	4	60	2026-05-29 04:55:19.127568	2026-05-29 04:55:19.127568	f
11	22	Введение в искусственный интеллект	Основные понятия и история развития ИИ		Искусственный интеллект - это область компьютерных наук, занимающаяся созданием систем, способных выполнять задачи, требующие человеческого интеллекта.	1	20	2026-05-11 19:51:49.591887	2026-05-22 13:56:45.512566	f
14	23	Основы Python	Синтаксис и базовые конструкции		Переменные и типы данных\r\nPython - интерпретируемый язык программирования. В нем переменные объявляются без указания типа, а тип данных определяется автоматически при присвоении значения.	1	25	2026-05-11 19:51:54.446263	2026-05-22 13:56:45.512566	f
15	23	Pandas для анализа данных	Работа с DataFrame		Библиотека Pandas\r\nБиблиотека Pandas предоставляет мощные инструменты для анализа и обработки данных. Основные структуры данных: DataFrame (таблицы) и Series (одномерные массивы).	2	30	2026-05-11 19:51:54.446263	2026-05-22 13:56:45.512566	f
171	67	Еще один урок	\N		....	7	10	2026-05-29 12:00:58.165278	2026-05-29 12:00:58.165278	f
122	60	Урок 4: Нормализация	\N		Цель: Избавиться от хаоса в данных.\n\nТеория: Нормализация — правила проектирования. Простое правило: «Один факт — в одном месте».\n\nПлохо: В одной ячейке «Иванов, Петров, Сидоров».\n\nХорошо: Три отдельные строки или связь с другой таблицей.\n\nАнтипример: Хранить возраст (меняется каждый год). Правильно: Хранить дату рождения.\n\nПрактика: Дана таблица «Заказ» (Номер_заказа, Товары_через_запятую). Предложите, как разбить её на 2 правильные таблицы.	4	25	2026-05-25 12:08:30.320686	2026-05-25 12:08:30.320686	f
126	61	Урок 3: Функции, стек вызовов и передача параметров	\N		Цель: Увидеть, как работает механизм вызова функции.\n\nТеория:\n\nПри вызове функции создаётся новый кадр стека (stack frame). В него кладутся локальные переменные и аргументы.\n\nПередача по значению (call by value): В функцию копируются значения аргументов. Функция работает с копиями, оригинал не меняется.\n\nПередача по указателю: В функцию копируется адрес. Функция может изменить оригинал через разыменование.\n\nСравнение:\n\nc\nvoid bad_swap(int a, int b) { int t = a; a = b; b = t; }  // Не работает!\nvoid good_swap(int *a, int *b) { int t = *a; *a = *b; *b = t; } // Работает\nПрактика: Напишите функцию increment, которая увеличивает переданное целое число на 1. Два варианта: (1) передача по значению — почему не сработает? (2) передача по указателю — рабочий вариант.	3	30	2026-05-25 13:06:16.617528	2026-05-25 13:06:16.617528	f
12	22	Машинное обучение	Типы машинного обучения и их применение		Типы ML\r\nВ машинном обучении выделяют три основных типа: обучение с учителем, обучение без учителя и обучение с подкреплением.'	2	25	2026-05-11 19:51:49.591887	2026-05-22 13:56:45.512566	f
4	1	Регулирование цифровой экономики	Законы и нормативные акты, регулирующие IT-сферу	https://www.youtube.com/embed/dQw4w9WgXcQ	Правовое регулирование\r\n\r\nGDPR в Европе, закон о персональных данных в РФ, регулирование маркетплейсов.\r\n\r\nНовые вызовы: регулирование ИИ, криптовалют, цифровых платформ.	4	12	2026-04-29 17:40:50.031602	2026-05-22 13:56:45.512566	f
65	45	Введение в эволюцию данных и программного обеспечения	Автоматически импортировано из syllabus.\nОригинал: Introduction to Data and Software Evolution\nПеревод: Введение в эволюцию данных и программного обеспечения	\N	# Введение в эволюцию данных и программного обеспечения\n\nИзучите материалы по теме: Introduction to Data and Software Evolution	1	60	2026-05-21 14:46:46.563326	2026-05-22 13:56:45.512566	f
66	45	Представление данных в вычислительных системах	Автоматически импортировано из syllabus.\nОригинал: Data Representation in Computing Systems\nПеревод: Представление данных в вычислительных системах	\N	# Представление данных в вычислительных системах\n\nИзучите материалы по теме: Data Representation in Computing Systems	2	60	2026-05-21 14:46:46.563326	2026-05-22 13:56:45.512566	f
10	18	🌍 Россия. Применение курса.	Применение курса "Математический анализ"	https://www.youtube.com/embed/dQw4w9WgXcQ	🌍 Россия. Применение курса "Математический анализ"\r\n\r\nПрименение в России:\r\n• Авиастроение — расчёт нагрузок на крыло (КБ Сухого, Туполева)\r\n• Ракетно-космическая отрасль — расчёт траекторий (Роскосмос)\r\n• Нефтегазовая отрасль — оптимизация трубопроводов (Газпром)\r\n• Финансовый сектор — моделирование рисков (Сбер, ВТБ)\r\n• IT и GameDev — физика, анимация, машинное обучение\r\n\r\nИдеи для сотрудничества стран БРИКС:\r\n• Совместные исследования с Китаем в области вычислительной математики\r\n• Обмен студентами с индийскими IIT\r\n• Летние школы по математике для студентов из Бразилии и ЮАР\r\n• Совместные онлайн-олимпиады стран БРИКС\r\n\r\nКлючевые выводы:\r\n• Матанализ — основа физики, механики, экономики и data science\r\n• Интегралы позволяют вычислять площади, объёмы и работу\r\n• В России матанализ — фундамент инженерного образования	5	32	2026-04-29 17:41:43.179862	2026-05-22 13:56:45.512566	t
133	1	🌍 Россия. Применение курса.	Урок БРИКС	https://vkvideo.ru/video-68049176_456240802?list=ln-bDrVh2M7Hrt02ZzFBX	В России применяется след образом	9	10	2026-05-28 16:38:19.114596	2026-05-28 16:38:19.114596	t
141	62	Введение в эволюцию данных и программного обеспечения	Определение основных понятий: сигналы, данные, информация и знания; коэволюция данных и программного обеспечения; обзор структур данных и их роли в решении вычислительных задач.	\N	# Введение в эволюцию данных и программного обеспечения\n\nОпределение основных понятий: сигналы, данные, информация и знания; коэволюция данных и программного обеспечения; обзор структур данных и их роли в решении вычислительных задач.	1	60	2026-05-29 05:11:56.694584	2026-05-29 05:11:56.694584	f
67	45	Линейные структуры данных	Автоматически импортировано из syllabus.\nОригинал: Linear Data Structures\nПеревод: Линейные структуры данных	\N	# Линейные структуры данных\n\nИзучите материалы по теме: Linear Data Structures	3	60	2026-05-21 14:46:46.563326	2026-05-22 13:56:45.512566	f
68	45	Иерархические структуры данных	Автоматически импортировано из syllabus.\nОригинал: Hierarchical Data Structures\nПеревод: Иерархические структуры данных	\N	# Иерархические структуры данных\n\nИзучите материалы по теме: Hierarchical Data Structures	4	60	2026-05-21 14:46:46.563326	2026-05-22 13:56:45.512566	f
69	45	Стандартная библиотека шаблонов (STL) и отраслевые форматы	Автоматически импортировано из syllabus.\nОригинал: Standard Template Library (STL) and Industry Formats\nПеревод: Стандартная библиотека шаблонов (STL) и отраслевые форматы	\N	# Стандартная библиотека шаблонов (STL) и отраслевые форматы\n\nИзучите материалы по теме: Standard Template Library (STL) and Industry Formats	5	60	2026-05-21 14:46:46.563326	2026-05-22 13:56:45.512566	f
70	45	Анализ алгоритмов и сложность	Автоматически импортировано из syllabus.\nОригинал: Algorithm Analysis and Complexity\nПеревод: Анализ алгоритмов и сложность	\N	# Анализ алгоритмов и сложность\n\nИзучите материалы по теме: Algorithm Analysis and Complexity	6	60	2026-05-21 14:46:46.563326	2026-05-22 13:56:45.512566	f
71	45	Абстрактные типы данных и объектно-ориентированный дизайн	Автоматически импортировано из syllabus.\nОригинал: Abstract Data Types and Object-Oriented Design\nПеревод: Абстрактные типы данных и объектно-ориентированный дизайн	\N	# Абстрактные типы данных и объектно-ориентированный дизайн\n\nИзучите материалы по теме: Abstract Data Types and Object-Oriented Design	7	60	2026-05-21 14:46:46.563326	2026-05-22 13:56:45.512566	f
72	45	Управление памятью и системная иерархия	Автоматически импортировано из syllabus.\nОригинал: Memory Management and System Hierarchy\nПеревод: Управление памятью и системная иерархия	\N	# Управление памятью и системная иерархия\n\nИзучите материалы по теме: Memory Management and System Hierarchy	8	60	2026-05-21 14:46:46.563326	2026-05-22 13:56:45.512566	f
73	47	1	\N	1	1	1	1	2026-05-21 14:51:08.448467	2026-05-22 13:56:45.512566	f
127	61	Урок 4: Арифметика указателей и массивы	\N		Цель: Понять, что массив и указатель — это почти одно и то же (но не совсем).\n\nТеория:\n\nИмя массива — это константный указатель на его первый элемент. int arr[5]; → arr эквивалентно &arr[0].\n\nАрифметика указателей: Если p указывает на элемент массива, то p+1 указывает на следующий элемент. Смещение в байтах зависит от типа: int *p; p+1 сдвигается на sizeof(int) байт.\n\nДоступ по индексу — синтаксический сахар: arr[i] эквивалентно *(arr + i).\n\nВажное предупреждение: Массив не равен указателю в одном важном случае: sizeof(arr) вернёт размер всего массива, а sizeof(ptr) — размер указателя (8 байт на 64-битной системе).\n\nПрактика:\n\nc\nint nums[] = {10, 20, 30, 40};\nint *p = nums;\nprintf("%d", *(p + 2));  // Что выведет?\np++;\nprintf("%d", *p);        // А теперь?	4	35	2026-05-25 13:06:48.547341	2026-05-25 13:06:48.547341	f
123	60	🌍 Индия. Применение курса.	\N		Цель: Увидеть масштаб БД в реальной стране.\n\nИндия — IT-гигант с 1,4 млрд граждан. Без БД её управление было бы невозможным. Главный пример — Aadhaar (Уникальный идентификационный номер).\n\nЧто это: Самая большая в мире биометрическая база данных (содержит отпечатки пальцев и сканы радужки).\n\nКак работает под капотом: Представьте таблицу Граждане с 1,4 миллиарда строк. К ней привязаны таблицы: Паспорт, Субсидии_на_газ, Банковские_счета, Мобильные_номера.\n\nПрименение в жизни:\n\nБанкинг: Бедный фермер в Пенджабе получает кредит, потому что банк через SQL-запрос мгновенно проверяет его ID в системе Aadhaar.\nПродовольствие: Магазины дешёвых продуктов (PDS) используют БД, чтобы исключить «мертвые души» — если человек умер, база данных отключает выдачу риса и муки.\nЗарплаты (MNREGA): 100 млн семей получают зарплату за работу на стройках напрямую на счёт. БД следит, чтобы никто не получил деньги за «работу в воскресенье» (логика через CHECK или триггеры).\nРеальный кейс (2023-24 гг.): БД используется во время выборов. Политическая партия скачивает срезы данных: «Найти всех избирателей в районе Дели, кому от 20 до 30 лет, кто пользуется Ola (такси) и подписан на Netflix». Это позволяет делать нацеленную рекламу в WhatsApp.\n\nВывод для учеников: Изучая SELECT * FROM Users WHERE ..., вы учитесь управлять такой же сложной системой, как цифровое правительство Индии.	5	20	2026-05-25 12:09:50.969272	2026-05-25 12:09:50.969272	t
172	67	🌍 Китай. Применение курса.	\N		телефоны в Китае очень крутые	8	10	2026-05-29 12:01:29.318317	2026-05-29 12:01:29.318317	t
179	68	🌍 Китай. Применение курса.	\N	1	вот так	7	1	2026-05-31 16:39:29.43241	2026-05-31 16:39:29.43241	t
119	60	Урок 1: Что такое база данных	\N		Теория: База данных (БД) — это структурированное хранилище. В отличие от файла, БД позволяет быстро искать, обновлять и не терять данные при сбое.\n\nКлючевое понятие: Таблица, строка (запись), столбец (поле).\n\nПример: Телефонная книга. Вместо одного сплошного текста — столбцы: Имя, Фамилия, Номер.\n\nПрактика: Откройте любую таблицу в Excel и попробуйте объяснить, почему её нельзя считать полноценной БД (нет контроля типов данных, нет связей).	1	30	2026-05-25 12:06:49.586145	2026-05-25 12:06:49.586145	f
124	61	Урок 1: Модель памяти и переменные	\N	https://vkvideo.ru/video-145052891_456249232	Теория:\n\nКомпилятор выделяет переменной адрес в памяти (например, 0x7FFF1234).\n\nlvalue (left value): Выражение, обозначающее место в памяти (может стоять слева от =). Пример: int x = 5; — здесь x это lvalue.\n\nrvalue (right value): Выражение, обозначающее значение (не может стоять слева от =). Пример: 5 это rvalue. Нельзя написать 5 = x;.\n\nРазмеры типов: sizeof(char) = 1 байт, sizeof(int) зависит от архитектуры (обычно 4 байта на x86_64).\n\nПрактика:\n\nc\nint a = 10;\nint b = a;  // Что здесь происходит? Копируется значение (10).\na = 20;     // Изменится ли b? Нет.\nОбъясните, почему b осталось равно 10, используя понятие «разные участки памяти».	1	50	2026-05-25 13:04:02.401901	2026-05-25 13:04:02.401901	f
128	61	🌍 Индия. Применение курса.	\N		Цель урока: Познакомить студентов с опытом использования языка C в критических информационных системах Индии, показать межстрановое значение изучаемых технологий.\n\n1. Применение темы курса в Индии\nЯзык C является базовым инструментом разработки для высоконагруженных и встраиваемых систем в Индии. В условиях ограниченных аппаратных ресурсов и необходимости обслуживать сотни миллионов пользователей индийские инженеры используют C для достижения максимальной производительности и предсказуемости работы программ.\n\nКлючевые области применения:\n\nФинансовые транзакции (система UPI)\n\nТранспорт (бронирование билетов IRCTC)\n\nПромышленная автоматика (контроль дамб, ирригационных систем)\n\n2. Примеры из местной практики\nПример 1. Система мгновенных платежей UPI\nСистема обрабатывает более 10 миллиардов транзакций в месяц. Критический по времени обработки код (проверка подлинности, резервирование средств) реализован на C. Использование статических массивов и битовых масок позволяет укладываться в требуемое время отклика (менее 1 секунды на операцию).\n\nПример 2. Железнодорожная система IRCTC\nЕжедневно продаётся более 20 миллионов билетов. Система резервирования мест использует битовые поля для хранения статуса каждого места (1 бит на место). Это позволяет обрабатывать тысячи одновременных запросов без выделения динамической памяти.\n\nПример 3. Система мониторинга уровня воды (штат Карнатака)\nМикроконтроллеры на C собирают данные с датчиков каждые 5 секунд. При превышении порога автоматически открываются шлюзы. Отсутствие динамического выделения памяти гарантирует бесперебойную работу системы годами.\n\n3. Идеи для международного сотрудничества\nВ рамках стран BRICS предлагаются следующие форматы взаимодействия:\n\nФормат\tОписание\nОбмен лабораторными работами\tВузы России и Индии создают общий банк заданий по C с фокусом на ограничения памяти и реальное время\nСовместная олимпиада\tОнлайн-соревнование по программированию на C с ограничением памяти (128 КБ) и запретом на динамическое выделение\nДвуязычный учебник\tРазработка русско-английского (или русско-хинди) сборника задач с примерами из критических систем обеих стран\nАкадемический обмен\tКраткосрочные курсы для преподавателей: российский лектор читает модуль по системам реального времени в Индии, индийский — по битовым оптимизациям в России\n4. Ключевые выводы курса\nЯзык C остаётся востребованным инструментом для создания высоконадёжных и производительных систем.\n\nПонимание работы с памятью (указатели, статические массивы, битовые поля) — обязательная компетенция инженера, претендующего на работу в телекоммуникационной, финансовой или оборонной отрасли.\n\nОпыт Индии показывает, что грамотное использование C позволяет решать задачи миллиардного масштаба на ограниченном оборудовании.\n\nМеждународное сотрудничество в рамках BRICS даёт студентам доступ к уникальным практическим кейсам и расширяет профессиональный кругозор.	5	60	2026-05-25 13:15:57.334162	2026-05-25 13:15:57.334162	t
134	38	Интересные факты	Дополнительный урок от преподавателя	...	...	2	20	2026-05-28 16:43:11.26883	2026-05-28 16:43:11.26883	f
135	1	Блокчейн и криптовалюты	Дополнительный урок от преподавателя	33	Что такое блокчейн?\nБлокчейн — это распределённая база данных, где информация хранится в виде цепочки блоков. Каждый блок содержит данные и связан с предыдущим с помощью криптографии.\n\nОсновные свойства блокчейна:\n• Децентрализация — нет единого сервера\n• Неизменяемость данных — нельзя изменить прошлые записи\n• Прозрачность — все транзакции видны участникам\n\nКриптовалюты (Биткоин, Эфириум) — это цифровые деньги, работающие на блокчейне. Они позволяют совершать переводы без посредников (банков).\n\nПрименение в России:\n• Тестирование цифрового рубля (Банк России)\n• Учёт товаров в цепочках поставок\n• Системы голосования\n\nКлючевой вывод: блокчейн — это не только криптовалюты, но и технология для создания доверия в цифровом мире.	8	20	2026-05-28 17:37:10.800794	2026-05-28 17:37:10.800794	f
132	1	Искусственный интеллект в экономике	Дополнительный урок от преподавателя	22	Искусственный интеллект (ИИ) и его роль в цифровой экономике\r\nИскусственный интеллект — это способность компьютеров выполнять задачи, требующие человеческого интеллекта: распознавание образов, принятие решений, обработка естественного языка.\r\n\r\nГде применяется ИИ сегодня:\r\n• Финансовый сектор — скоринг клиентов, обнаружение мошенничества\r\n• Ритейл — рекомендательные системы (Ozon, Wildberries)\r\n• Логистика — оптимизация маршрутов доставки\r\n• Медицина — диагностика по снимкам\r\n\r\nПримеры в России:\r\n• Сбер — голосовой ассистент, система распознавания лиц\r\n• Яндекс — алгоритмы поиска, беспилотные автомобили\r\n• Тинькофф — чат-бот, кредитный скоринг на основе ИИ\r\n\r\nВызовы и риски:\r\n• Этические вопросы (предвзятость алгоритмов)\r\n• Безопасность данных\r\n• Регулирование (закон об ИИ в РФ)\r\n\r\nКлючевой вывод: ИИ становится ключевым фактором конкурентоспособности компаний в цифровой экономике.	7	10	2026-05-28 16:32:34.808566	2026-05-28 16:32:34.808566	f
142	62	Представление данных в вычислительных системах	Системы счисления: двоичная, восьмеричная, десятичная, шестнадцатеричная; алгоритмы перевода между различными системами счисления; примитивные типы данных и их представление в памяти.	\N	# Представление данных в вычислительных системах\n\nСистемы счисления: двоичная, восьмеричная, десятичная, шестнадцатеричная; алгоритмы перевода между различными системами счисления; примитивные типы данных и их представление в памяти.	2	60	2026-05-29 05:11:56.694584	2026-05-29 05:11:56.694584	f
143	62	Линейные структуры данных	Последовательности и массивы: непрерывное выделение памяти; связанные списки: динамическое распределение памяти и организация на основе узлов; наборы: неупорядоченные коллекции с уникальными элементами; карты (словари): ассоциации ключ-значение.	\N	# Линейные структуры данных\n\nПоследовательности и массивы: непрерывное выделение памяти; связанные списки: динамическое распределение памяти и организация на основе узлов; наборы: неупорядоченные коллекции с уникальными элементами; карты (словари): ассоциации ключ-значение.	3	60	2026-05-29 05:11:56.694584	2026-05-29 05:11:56.694584	f
144	62	Иерархические структуры данных	Деревья: основные понятия и терминология; бинарные деревья и бинарные деревья поиска; алгоритмы обхода дерева (предварительный, по порядку, пост-порядок, поуровневый)	\N	# Иерархические структуры данных\n\nДеревья: основные понятия и терминология; бинарные деревья и бинарные деревья поиска; алгоритмы обхода дерева (предварительный, по порядку, пост-порядок, поуровневый)	4	60	2026-05-29 05:11:56.694584	2026-05-29 05:11:56.694584	f
145	62	Стандартная библиотека шаблонов (STL) и отраслевые форматы	Назначение и состав библиотеки STL; контейнеры: контейнеры последовательностей, ассоциативные контейнеры, адаптеры контейнеров; итераторы: категории и шаблоны использования; алгоритмы: типовые операции над контейнерами; форматы файлов данных: CSV, JSON, XML.	\N	# Стандартная библиотека шаблонов (STL) и отраслевые форматы\n\nНазначение и состав библиотеки STL; контейнеры: контейнеры последовательностей, ассоциативные контейнеры, адаптеры контейнеров; итераторы: категории и шаблоны использования; алгоритмы: типовые операции над контейнерами; форматы файлов данных: CSV, JSON, XML.	5	60	2026-05-29 05:11:56.694584	2026-05-29 05:11:56.694584	f
146	62	Анализ алгоритмов и сложность	Введение в алгоритмы: определение и свойства; анализ сложности: соображения времени и пространства; Обозначение Big-Oh и асимптотический анализ; анализ операций со структурами данных.	\N	# Анализ алгоритмов и сложность\n\nВведение в алгоритмы: определение и свойства; анализ сложности: соображения времени и пространства; Обозначение Big-Oh и асимптотический анализ; анализ операций со структурами данных.	6	60	2026-05-29 05:11:56.694584	2026-05-29 05:11:56.694584	f
120	60	Урок 2: Основной язык — SQL	\N		Теория: SQL (Structured Query Language) — язык, на котором мы говорим с БД. Самая частая команда — SELECT (выборка).\n\nСинтаксис:\n\nsql\nSELECT Фамилия, Имя FROM Студенты WHERE Город = 'Москва';\nПрактика: Напишите запрос, который выводит список товаров дороже 1000 рублей из таблицы «Товары».	2	30	2026-05-25 12:07:14.467893	2026-05-25 12:07:14.467893	f
125	61	Урок 2: Указатели	\N	https://vkvideo.ru/video-182769281_456239112	Теория:\n\nУказатель хранит адрес другой переменной.\n\nint *p; — объявление указателя на int (читается: «p есть указатель на int»).\n\n& — оператор взятия адреса. p = &a; — теперь p указывает на a.\n\n* — оператор разыменования. *p = 10; — записать 10 в ту память, на которую указывает p (то есть в a).\n\nКлючевая идея: Указатели позволяют одной функции изменить переменную, объявленную в другой функции (передача по ссылке).\n\nПрактика:\n\nc\nint x = 5;\nint *ptr = &x;\n*ptr = 7;\nprintf("%d", x);  // Что выведет?\nОбъясните, почему изменилось x.	2	30	2026-05-25 13:05:43.41441	2026-05-25 13:05:43.41441	f
190	71	🌍 Китай. Применение курса.	\N	12	авыав	7	10	2026-06-01 18:53:48.597388	2026-06-01 18:53:48.597388	t
147	63	Большие данные. Основы систем больших данных.	Понятие 'большие данные', характеристики, принципы работы, экосистема больших данных.	\N	# Большие данные. Основы систем больших данных.\n\nПонятие 'большие данные', характеристики, принципы работы, экосистема больших данных.	1	60	2026-05-29 06:50:58.739804	2026-05-29 06:50:58.739804	f
148	63	Архитектура системы обработки больших данных.	Уровни в системах обработки больших данных, сбор, хранение и представление данных.	\N	# Архитектура системы обработки больших данных.\n\nУровни в системах обработки больших данных, сбор, хранение и представление данных.	2	60	2026-05-29 06:50:58.739804	2026-05-29 06:50:58.739804	f
149	63	Параллельные алгоритмы для работы с данными.	Алгоритмы Map и Reduce, лямбда- и каппа-архитектуры.	\N	# Параллельные алгоритмы для работы с данными.\n\nАлгоритмы Map и Reduce, лямбда- и каппа-архитектуры.	3	60	2026-05-29 06:50:58.739804	2026-05-29 06:50:58.739804	f
150	63	Программные платформы и системы для больших данных.	Системы потоков данных, хранения больших данных, аналитические платформы.	\N	# Программные платформы и системы для больших данных.\n\nСистемы потоков данных, хранения больших данных, аналитические платформы.	4	60	2026-05-29 06:50:58.739804	2026-05-29 06:50:58.739804	f
151	63	Системы управления большими данными.	Анализ программных комплексов Apache Hadoop, принципы работы Spark и другие компоненты экосистемы Hadoop.	\N	# Системы управления большими данными.\n\nАнализ программных комплексов Apache Hadoop, принципы работы Spark и другие компоненты экосистемы Hadoop.	5	60	2026-05-29 06:50:58.739804	2026-05-29 06:50:58.739804	f
152	64	Введение в эволюцию данных и программного обеспечения	Историческая перспектива и коэволюция данных и программного обеспечения	\N	# Введение в эволюцию данных и программного обеспечения\n\nИсторическая перспектива и коэволюция данных и программного обеспечения	1	60	2026-05-29 06:54:26.355128	2026-05-29 06:54:26.355128	f
153	64	Представление данных в вычислительных системах	Системы счисления, типы данных и иерархии памяти	\N	# Представление данных в вычислительных системах\n\nСистемы счисления, типы данных и иерархии памяти	2	60	2026-05-29 06:54:26.355128	2026-05-29 06:54:26.355128	f
154	64	Линейные структуры данных	Массивы, связанные списки, наборы и карты	\N	# Линейные структуры данных\n\nМассивы, связанные списки, наборы и карты	3	60	2026-05-29 06:54:26.355128	2026-05-29 06:54:26.355128	f
155	64	Иерархические структуры данных	Деревья, бинарные деревья и сбалансированные деревья.	\N	# Иерархические структуры данных\n\nДеревья, бинарные деревья и сбалансированные деревья.	4	60	2026-05-29 06:54:26.355128	2026-05-29 06:54:26.355128	f
156	64	Стандартная библиотека шаблонов (STL) и отраслевые форматы	Контейнеры STL, итераторы и форматы файлов данных.	\N	# Стандартная библиотека шаблонов (STL) и отраслевые форматы\n\nКонтейнеры STL, итераторы и форматы файлов данных.	5	60	2026-05-29 06:54:26.355128	2026-05-29 06:54:26.355128	f
157	64	Анализ алгоритмов и сложность	Алгоритмы, анализ сложности и нотация big-O	\N	# Анализ алгоритмов и сложность\n\nАлгоритмы, анализ сложности и нотация big-O	6	60	2026-05-29 06:54:26.355128	2026-05-29 06:54:26.355128	f
160	66	Представление данных в вычислительных системах	Системы счисления: двоичная, восьмеричная, десятичная, шестнадцатеричная; алгоритмы перевода между различными системами счисления; примитивные типы данных и их представление в памяти.	\N	# Представление данных в вычислительных системах\n\nСистемы счисления: двоичная, восьмеричная, десятичная, шестнадцатеричная; алгоритмы перевода между различными системами счисления; примитивные типы данных и их представление в памяти.	2	60	2026-05-29 07:07:52.362109	2026-05-29 07:07:52.362109	f
159	66	Введение в эволюцию данных и программного обеспечения	Определение основных понятий: сигналы, данные, информация и знания; коэволюция данных и программного обеспечения; обзор структур данных и их роли в решении вычислительных задач.		# Введение в эволюцию данных и программного обеспечения\n\nОпределение основных понятий: сигналы, данные, информация и знания; коэволюция данных и программного обеспечения; обзор структур данных и их роли в решении вычислительных задач.	1	60	2026-05-29 07:07:52.362109	2026-05-29 07:07:52.362109	f
195	72	Стандартная библиотека шаблонов (STL) и отраслевые форматы	Контейнеры STL, итераторы, алгоритмы и форматы файлов данных.	\N	# Стандартная библиотека шаблонов (STL) и отраслевые форматы\n\nКонтейнеры STL, итераторы, алгоритмы и форматы файлов данных.	5	60	2026-06-03 11:57:08.350481	2026-06-03 11:57:08.350481	f
196	72	Анализ алгоритмов и сложность	Алгоритмы, анализ сложности и нотация Big-O	\N	# Анализ алгоритмов и сложность\n\nАлгоритмы, анализ сложности и нотация Big-O	6	60	2026-06-03 11:57:08.350481	2026-06-03 11:57:08.350481	f
161	66	Линейные структуры данных	Последовательности и массивы: непрерывное выделение памяти; связанные списки: динамическое распределение памяти и организация на основе узлов; наборы: неупорядоченные коллекции с уникальными элементами; карты (словари): ассоциации ключ-значение.	\N	# Линейные структуры данных\n\nПоследовательности и массивы: непрерывное выделение памяти; связанные списки: динамическое распределение памяти и организация на основе узлов; наборы: неупорядоченные коллекции с уникальными элементами; карты (словари): ассоциации ключ-значение.	3	60	2026-05-29 07:07:52.362109	2026-05-29 07:07:52.362109	f
162	66	Иерархические структуры данных	Деревья: основные понятия и терминология; бинарные деревья и бинарные деревья поиска; алгоритмы обхода дерева (предварительный, по порядку, пост-порядок, поуровневый)	\N	# Иерархические структуры данных\n\nДеревья: основные понятия и терминология; бинарные деревья и бинарные деревья поиска; алгоритмы обхода дерева (предварительный, по порядку, пост-порядок, поуровневый)	4	60	2026-05-29 07:07:52.362109	2026-05-29 07:07:52.362109	f
163	66	Стандартная библиотека шаблонов (STL) и отраслевые форматы	Назначение и состав библиотеки STL; контейнеры: контейнеры последовательностей, ассоциативные контейнеры, адаптеры контейнеров; итераторы: категории и шаблоны использования; алгоритмы: типовые операции над контейнерами; форматы файлов данных: CSV, JSON, XML.	\N	# Стандартная библиотека шаблонов (STL) и отраслевые форматы\n\nНазначение и состав библиотеки STL; контейнеры: контейнеры последовательностей, ассоциативные контейнеры, адаптеры контейнеров; итераторы: категории и шаблоны использования; алгоритмы: типовые операции над контейнерами; форматы файлов данных: CSV, JSON, XML.	5	60	2026-05-29 07:07:52.362109	2026-05-29 07:07:52.362109	f
164	66	Анализ алгоритмов и сложность	Введение в алгоритмы: определение и свойства; анализ сложности: соображения времени и пространства; Обозначение Big-Oh и асимптотический анализ; анализ операций со структурами данных.	\N	# Анализ алгоритмов и сложность\n\nВведение в алгоритмы: определение и свойства; анализ сложности: соображения времени и пространства; Обозначение Big-Oh и асимптотический анализ; анализ операций со структурами данных.	6	60	2026-05-29 07:07:52.362109	2026-05-29 07:07:52.362109	f
165	67	Введение в эволюцию данных и программного обеспечения	Определение основных понятий: сигналы, данные, информация и знания; коэволюция данных и программного обеспечения; обзор структур данных и их роли в решении вычислительных задач.	\N	# Введение в эволюцию данных и программного обеспечения\n\nОпределение основных понятий: сигналы, данные, информация и знания; коэволюция данных и программного обеспечения; обзор структур данных и их роли в решении вычислительных задач.	1	60	2026-05-29 11:59:40.782895	2026-05-29 11:59:40.782895	f
166	67	Представление данных в вычислительных системах	Системы счисления: двоичная, восьмеричная, десятичная, шестнадцатеричная; алгоритмы перевода между различными системами счисления; примитивные типы данных и их представление в памяти.	\N	# Представление данных в вычислительных системах\n\nСистемы счисления: двоичная, восьмеричная, десятичная, шестнадцатеричная; алгоритмы перевода между различными системами счисления; примитивные типы данных и их представление в памяти.	2	60	2026-05-29 11:59:40.782895	2026-05-29 11:59:40.782895	f
167	67	Линейные структуры данных	Последовательности и массивы: непрерывное выделение памяти; связанные списки: динамическое распределение памяти и организация на основе узлов; наборы: неупорядоченные коллекции с уникальными элементами; карты (словари): ассоциации ключ-значение.	\N	# Линейные структуры данных\n\nПоследовательности и массивы: непрерывное выделение памяти; связанные списки: динамическое распределение памяти и организация на основе узлов; наборы: неупорядоченные коллекции с уникальными элементами; карты (словари): ассоциации ключ-значение.	3	60	2026-05-29 11:59:40.782895	2026-05-29 11:59:40.782895	f
168	67	Иерархические структуры данных	Деревья: основные понятия и терминология; бинарные деревья и бинарные деревья поиска; алгоритмы обхода дерева (предварительный, по порядку, пост-порядок, поуровневый)	\N	# Иерархические структуры данных\n\nДеревья: основные понятия и терминология; бинарные деревья и бинарные деревья поиска; алгоритмы обхода дерева (предварительный, по порядку, пост-порядок, поуровневый)	4	60	2026-05-29 11:59:40.782895	2026-05-29 11:59:40.782895	f
197	72	🌍 Китай. Применение курса.	\N	ту	коко	7	1	2026-06-03 11:57:46.666209	2026-06-03 11:57:46.666209	t
169	67	Стандартная библиотека шаблонов (STL) и отраслевые форматы	Назначение и состав библиотеки STL; контейнеры: контейнеры последовательностей, ассоциативные контейнеры, адаптеры контейнеров; итераторы: категории и шаблоны использования; алгоритмы: типовые операции над контейнерами; форматы файлов данных: CSV, JSON, XML.	\N	# Стандартная библиотека шаблонов (STL) и отраслевые форматы\n\nНазначение и состав библиотеки STL; контейнеры: контейнеры последовательностей, ассоциативные контейнеры, адаптеры контейнеров; итераторы: категории и шаблоны использования; алгоритмы: типовые операции над контейнерами; форматы файлов данных: CSV, JSON, XML.	5	60	2026-05-29 11:59:40.782895	2026-05-29 11:59:40.782895	f
170	67	Анализ алгоритмов и сложность	Введение в алгоритмы: определение и свойства; анализ сложности: соображения времени и пространства; Обозначение Big-Oh и асимптотический анализ; анализ операций со структурами данных; лучший, средний и худший сценарии.	\N	# Анализ алгоритмов и сложность\n\nВведение в алгоритмы: определение и свойства; анализ сложности: соображения времени и пространства; Обозначение Big-Oh и асимптотический анализ; анализ операций со структурами данных; лучший, средний и худший сценарии.	6	60	2026-05-29 11:59:40.782895	2026-05-29 11:59:40.782895	f
173	68	Введение в эволюцию данных и программного обеспечения	Историческая перспектива и коэволюция данных и программного обеспечения	\N	# Введение в эволюцию данных и программного обеспечения\n\nИсторическая перспектива и коэволюция данных и программного обеспечения	1	60	2026-05-31 16:38:49.971682	2026-05-31 16:38:49.971682	f
174	68	Представление данных в вычислительных системах	Системы счисления, типы данных и иерархии памяти	\N	# Представление данных в вычислительных системах\n\nСистемы счисления, типы данных и иерархии памяти	2	60	2026-05-31 16:38:49.971682	2026-05-31 16:38:49.971682	f
175	68	Линейные структуры данных	Массивы, связанные списки, наборы и карты	\N	# Линейные структуры данных\n\nМассивы, связанные списки, наборы и карты	3	60	2026-05-31 16:38:49.971682	2026-05-31 16:38:49.971682	f
176	68	Иерархические структуры данных	Деревья, бинарные деревья и кучи	\N	# Иерархические структуры данных\n\nДеревья, бинарные деревья и кучи	4	60	2026-05-31 16:38:49.971682	2026-05-31 16:38:49.971682	f
177	68	Стандартная библиотека шаблонов (STL) и отраслевые форматы	Контейнеры STL, итераторы, алгоритмы и форматы файлов данных.	\N	# Стандартная библиотека шаблонов (STL) и отраслевые форматы\n\nКонтейнеры STL, итераторы, алгоритмы и форматы файлов данных.	5	60	2026-05-31 16:38:49.971682	2026-05-31 16:38:49.971682	f
178	68	Анализ алгоритмов и сложность	Алгоритмы, анализ сложности и нотация Big-O	\N	# Анализ алгоритмов и сложность\n\nАлгоритмы, анализ сложности и нотация Big-O	6	60	2026-05-31 16:38:49.971682	2026-05-31 16:38:49.971682	f
181	70	Переменные и основные типы данных.	\N	123	Переменная — это именованный контейнер для хранения данных. Представьте коробку с наклейкой, куда можно положить число, текст или правду/ложь.\n\nВ языках программирования (на примере Python):\n\npython\nage = 18               # целое число (int)\nname = "Анна"          # текст (str)\nis_student = True      # логическое значение (bool)\n\nОсновные типы данных:\n\nЧисла — для счёта (1, 2, 3...).\n\nСтроки — для текста (в кавычках).\n\nЛогический тип — True или False (да/нет).\n\nПеременные позволяют программе запоминать информацию и использовать её позже.	2	10	2026-06-01 17:50:31.798141	2026-06-01 17:50:31.798141	f
182	70	Условия (if-else) и циклы (for, while)	\N	123	Программы редко выполняются линейно — чаще нужно выбирать или повторять действия.\n\nУсловия выполняют код только при выполнении какого-то условия.\n\npython\nscore = 85\nif score >= 60:\n    print("Экзамен сдан")\nelse:\n    print("Нужно подучить")\nЦиклы позволяют повторять действия многократно.\n\nfor — используется, когда известно количество повторений.\n\npython\nfor i in range(5):\n    print("Привет!")   # напечатается 5 раз\nwhile — повторяет, пока условие истинно.\n\npython\ncount = 0\nwhile count < 3:\n    print(f"Повтор номер {count}")\n    count = count + 1\nУсловия и циклы — основа любой нетривиальной программы (игр, калькуляторов, приложений).	3	15	2026-06-01 17:51:05.961904	2026-06-01 17:51:05.961904	f
183	70	🌍 Китай. Применение курса.	\N	123	Примеры применения темы в Китае\nКитай — один из мировых лидеров в области цифровых технологий. Базовые навыки программирования (алгоритмы, переменные, циклы) используются повсеместно:\n\nПромышленность 4.0 – на заводах компании Huawei и BYD (производитель электромобилей) программируемые контроллеры управляют роботами на конвейерах. Условия (if-else) проверяют качество сборки, циклы (for) управляют повторяющимися операциями.\n\nФинтех и платежи – системы Alipay и WeChat Pay обрабатывают миллионы транзакций в секунду. Переменные хранят баланс пользователя, условия проверяют достаточно ли средств, циклы обрабатывают очереди платежей.\n\nОбразование – в Китае с 2020 года программирование входит в обязательную школьную программу. Дети учатся писать простые алгоритмы на Python, решать задачи с циклами и условиями на платформах типа CodeCombat и XuetangX.\n\nИдеи для международного сотрудничества\nСовместный онлайн-курс «Алгоритмы для зелёной энергетики» – студенты из России и Китая вместе программируют модели солнечных панелей или ветряков, используя переменные для расчёта мощности и циклы для сбора данных с датчиков.\n\nСоревнование по робототехнике BRICS – команды стран БРИКС программируют виртуальных роботов на языке Python (условия объезда препятствий, циклы движения). Лучшие решения публикуются в репозиторий платформы.\n\nОбмен учебными кейсами – китайские преподаватели делятся задачами по программированию для управления дронами (логистика), а российские – задачами по обработке геоданных (циклы для анализа спутниковых снимков). Кейсы добавляются в общую библиотеку курса.\n\nОбобщение ключевых выводов курса\nПрограммирование — это не только синтаксис, но и инструмент решения реальных задач: от автоматизации завода до финансовых расчётов.\n\nУсловия и циклы лежат в основе любой автоматизации – их понимание даёт возможность создавать полезные программы независимо от страны.\n\nСотрудничество между странами БРИКС в обучении программированию позволяет объединить лучшие практики, ускорить развитие технологий и подготовить кадры для совместных проектов.	4	15	2026-06-01 17:53:03.432569	2026-06-01 17:53:03.432569	t
180	70	Что такое программирование. Понятие алгоритма.	\N	123	Программирование — это способ общения с компьютером на языке, который он понимает. Компьютер выполняет строгие последовательности действий — алгоритмы.\n\nАлгоритм — это чёткая последовательность шагов, приводящая к результату. Пример алгоритма из жизни: рецепт приготовления чая.\n\nВ программировании алгоритмы записываются на специальных языках (Python, Java, C++). Компьютер выполняет инструкции строго по порядку.	1	10	2026-06-01 17:49:44.868572	2026-06-01 17:49:44.868572	f
184	71	Введение в эволюцию данных и программного обеспечения	Коэволюция данных и программного обеспечения, определения сигналов, данных, информации и знаний.	\N	# Введение в эволюцию данных и программного обеспечения\n\nКоэволюция данных и программного обеспечения, определения сигналов, данных, информации и знаний.	1	60	2026-06-01 18:17:38.985562	2026-06-01 18:17:38.985562	f
185	71	Представление данных в вычислительных системах	Системы счисления, типы данных, иерархии памяти и преобразования.	\N	# Представление данных в вычислительных системах\n\nСистемы счисления, типы данных, иерархии памяти и преобразования.	2	60	2026-06-01 18:17:38.985562	2026-06-01 18:17:38.985562	f
186	71	Линейные структуры данных	Последовательности, массивы, связанные списки, множества и карты.	\N	# Линейные структуры данных\n\nПоследовательности, массивы, связанные списки, множества и карты.	3	60	2026-06-01 18:17:38.985562	2026-06-01 18:17:38.985562	f
187	71	Иерархические структуры данных	Деревья, бинарные деревья, двоичные деревья поиска, кучи и очереди с приоритетами.	\N	# Иерархические структуры данных\n\nДеревья, бинарные деревья, двоичные деревья поиска, кучи и очереди с приоритетами.	4	60	2026-06-01 18:17:38.985562	2026-06-01 18:17:38.985562	f
188	71	Стандартная библиотека шаблонов (STL) и отраслевые форматы	Контейнеры STL, итераторы, алгоритмы и форматы файлов данных, такие как CSV, JSON, XML.	\N	# Стандартная библиотека шаблонов (STL) и отраслевые форматы\n\nКонтейнеры STL, итераторы, алгоритмы и форматы файлов данных, такие как CSV, JSON, XML.	5	60	2026-06-01 18:17:38.985562	2026-06-01 18:17:38.985562	f
189	71	Анализ алгоритмов и сложность	Алгоритмы, анализ сложности, нотация Big-O и эффективность алгоритмов.	\N	# Анализ алгоритмов и сложность\n\nАлгоритмы, анализ сложности, нотация Big-O и эффективность алгоритмов.	6	60	2026-06-01 18:17:38.985562	2026-06-01 18:17:38.985562	f
191	72	Введение в эволюцию данных и программного обеспечения	Историческая перспектива и коэволюция данных и программного обеспечения	\N	# Введение в эволюцию данных и программного обеспечения\n\nИсторическая перспектива и коэволюция данных и программного обеспечения	1	60	2026-06-03 11:57:08.350481	2026-06-03 11:57:08.350481	f
192	72	Представление данных в вычислительных системах	Системы счисления, типы данных и иерархии памяти	\N	# Представление данных в вычислительных системах\n\nСистемы счисления, типы данных и иерархии памяти	2	60	2026-06-03 11:57:08.350481	2026-06-03 11:57:08.350481	f
193	72	Линейные структуры данных	Массивы, связанные списки, наборы и карты	\N	# Линейные структуры данных\n\nМассивы, связанные списки, наборы и карты	3	60	2026-06-03 11:57:08.350481	2026-06-03 11:57:08.350481	f
194	72	Иерархические структуры данных	Деревья, бинарные деревья и сбалансированные деревья.	\N	# Иерархические структуры данных\n\nДеревья, бинарные деревья и сбалансированные деревья.	4	60	2026-06-03 11:57:08.350481	2026-06-03 11:57:08.350481	f
\.


--
-- TOC entry 3551 (class 0 OID 16503)
-- Dependencies: 227
-- Data for Name: test_questions; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.test_questions (id, test_id, question_text, option_a, option_b, option_c, option_d, correct_answer, points, "order") FROM stdin;
1	1	Что такое цифровая экономика?	Экономика, основанная на сельском хозяйстве	Экономика, основанная на цифровых технологиях	Экономика, основанная на тяжёлой промышленности	Экономика, основанная на ручном труде	B	1	1
2	1	Какой пример цифровой платформы?	Завод	Шахта	Ozon	Пшеничное поле	C	1	2
3	1	Что означает модель "Freemium"?	Бесплатный продукт без рекламы	Бесплатный продукт с возможностью платной подписки на дополнительные функции	Только платный продукт	Продукт только по подписке без бесплатной версии	B	1	3
4	1	Что такое SaaS?	Программное обеспечение как услуга	Аппаратное обеспечение как услуга	Производство как услуга	Логистика как услуга	A	1	4
5	1	Какой закон регулирует персональные данные в России?	GDPR	Закон о персональных данных	Закон об авторском праве	Закон о рекламе	B	1	5
6	2	Чему равен предел lim_{x→0} sin(x)/x?	0	1	∞	Не существует	B	1	1
7	2	Что такое производная функции в точке?	Площадь под графиком	Скорость изменения функции	Значение функции в точке	Обратная функция	B	1	2
8	2	Чему равна производная x³?	x²	3x²	3x³	x⁴	B	1	3
9	2	Что называется первообразной функции f(x)?	Функция F(x), производная которой равна f(x)	Функция F(x), интеграл которой равен f(x)	Предел функции f(x)	Производная функции f(x)	A	1	4
10	2	Как вычисляется определённый интеграл ∫ₐᵇ f(x)dx?	f(b) - f(a)	F(b) - F(a), где F'(x)=f(x)	F(a) - F(b)	f(a) - f(b)	B	1	5
11	2	Чему равен интеграл ∫1/x dx?	x + C	eˣ + C	ln|x| + C	sin x + C	C	1	6
12	2	Какая точка называется точкой максимума функции?	Точка, где производная равна 0 и слева производная >0, справа <0	Точка, где производная равна 0 и слева производная <0, справа >0	Точка, где производная не существует	Точка, где функция равна 0	A	1	7
13	2	Что такое e (число Эйлера)?	lim_{n→∞} (1 + 1/n)ⁿ	lim_{n→∞} (1 - 1/n)ⁿ	lim_{n→∞} (n + 1/n)	lim_{n→∞} (1/n)ⁿ	A	1	8
14	2	Производная от eˣ равна?	x·eˣ⁻¹	eˣ	ln x	1/x	B	1	9
15	2	Какой интеграл используется для вычисления площади под кривой?	Неопределённый интеграл	Определённый интеграл	Кратный интеграл	Криволинейный интеграл	B	1	10
16	3	Что такое искусственный интеллект (ИИ)?	Программирование компьютера на выполнение конкретных задач	Создание машин, которые могут мыслить и учиться как люди	Разработка баз данных	Написание веб-приложений	B	10	1
17	3	Какая из перечисленных задач НЕ относится к ИИ?	Распознавание изображений	Обработка естественного языка	Сортировка массива чисел	Игра в шахматы	C	10	2
18	3	Что такое нейронная сеть?	Компьютерная сеть для интернета	Математическая модель, вдохновленная биологическими нейронами	База данных для хранения информации	Язык программирования	B	10	3
19	3	Какой алгоритм используется для обучения нейронных сетей?	Метод Гаусса	Алгоритм Дейкстры	Метод обратного распространения ошибки	Бинарный поиск	C	10	4
20	3	Что такое "глубокое обучение" (Deep Learning)?	Простое машинное обучение	Многослойные нейронные сети	Обучение без данных	Сортировка данных	B	10	5
21	3	Какая библиотека Python чаще всего используется для создания нейросетей?	Pandas	NumPy	TensorFlow / PyTorch	Matplotlib	C	10	6
22	3	Что такое "обучение с учителем" (Supervised Learning)?	Обучение на размеченных данных	Обучение без ответов	Обучение с подкреплением	Обучение на видео	A	10	7
23	3	CNN в машинном обучении расшифровывается как:	Central Neural Network	Convolutional Neural Network	Computer Network Network	Circuit Neural Network	B	10	8
24	4	Что такое "Большие данные" (Big Data)?	Небольшой объем данных	Данные, которые не помещаются в Excel	Наборы данных большого объема, которые трудно обработать традиционными методами	Структурированные данные в SQL	C	10	1
25	4	Какие "3V" характеризуют Big Data?	Velocity, Volume, Variety	Value, Verification, Vision	Version, View, Virtual	Voice, Video, Volume	A	10	2
26	4	Что такое Hadoop?	База данных	Фреймворк для распределенной обработки больших данных	Язык программирования	Операционная система	B	10	3
27	4	Какой язык чаще всего используется для анализа больших данных?	Java	C++	Python / SQL	PHP	C	10	4
28	4	Что такое MapReduce?	Модель программирования для обработки больших данных	База данных NoSQL	Графический редактор	Антивирус	A	10	5
29	4	Что такое "Data Warehouse"?	Хранилище данных для аналитики	Обычная база данных	Облачное хранилище	Склад данных на жестком диске	A	10	6
30	4	Какая база данных является NoSQL?	PostgreSQL	MySQL	MongoDB	Oracle	C	10	7
31	4	Что такое "ETL" в аналитике данных?	Extract, Transform, Load	Edit, Transfer, Link	Execute, Test, Loop	Export, Translate, Launch	A	10	8
35	18	Что такое Activity в Android?	A) База данных	B) Экран приложения	C) Стиль оформления	D) Сетевой запрос	B	10	1
36	18	Какая библиотека используется для HTTP-запросов в Android?	A) Picasso	B) Retrofit	C) Glide	D) Room	B	10	2
37	18	Для чего нужен фрагмент (Fragment) в Android?	A) Для хранения паролей	B) Для переиспользуемой части экрана	C) Для воспроизведения видео	D) Для работы с GPS	B	10	3
38	19	с# это	А язык программирования	В набор символов	С что-то острое	D модель телефона	A	10	1
44	24	Основной язык базы данных	sql	c+	python	scl	A	10	1
45	25	Что из перечисленного является корректным объявлением указателя на переменную типа int	A) int ptr;	B) int &ptr;	C) int *ptr;	D) ptr int*;	C	10	1
46	25	2. Каким будет результат выполнения следующего фрагмента кода?\n\nint a = 5;\nint b = 10;\nint *p = &a;\n*p = 7;\nprintf("%d", a);	A) 5	B) 7	C) 10	D) Адрес переменной a	B	10	2
47	25	3. Оператор sizeof(char) в языке C возвращает:	A) 1 байт	B) 2 байта	C) 4 байта	D) Зависит от компилятора	A	10	3
48	25	4. Какая конструкция правильно описывает условный оператор в C?	A) if x > 0 then { printf("Положительное"); }	B) if (x > 0) { printf("Положительное"); }	C) if [x > 0] { printf("Положительное"); }	D) when (x > 0) { printf("Положительное"); }	B	10	4
49	25	5. Чему равно значение выражения 10 / 3 в языке C (оба операнда — целые числа)?	A) 3.333	B) 3	C) 3.0	D) Ошибка компиляции	B	10	5
50	26	1	1	2	3	4	A	10	1
51	26	3	1	2	3	4	C	10	2
52	27	с телефона все создалось	да	нет	не знаю	без ответа	A	10	1
53	29	2	2	2	2	2	B	10	1
54	30	2	2	2	2	2	B	10	1
55	31	11	1	2	3	4	A	10	1
\.


--
-- TOC entry 3545 (class 0 OID 16443)
-- Dependencies: 221
-- Data for Name: user_courses; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.user_courses (id, user_id, course_id, status, result, started_at, completed_at, created_at, updated_at) FROM stdin;
82	24	37	completed	100.00	\N	2026-05-12 13:51:00	2026-05-12 20:49:18.808848	2026-05-12 20:51:05.987545
83	34	1	completed	80.00	\N	2026-05-12 22:45:17	2026-05-12 22:44:51.264827	2026-05-12 22:45:18.323536
4	2	5	in_progress	\N	2026-04-29 15:23:01.115601	\N	2026-04-29 15:23:01.115601	2026-04-29 15:23:01.115601
84	34	18	in_progress	\N	\N	\N	2026-05-14 22:02:57.913817	2026-05-14 22:02:57.913817
2	1	2	completed	95.00	\N	2026-04-29 15:24:22.16689	2026-04-29 15:22:09.497497	2026-04-29 15:22:09.497497
1	1	1	completed	95.00	2026-04-29 15:23:56.313091	2026-04-29 15:25:20.538828	2026-04-29 15:22:09.497497	2026-04-29 15:22:09.497497
85	24	23	in_progress	\N	\N	\N	2026-05-20 13:47:49.486144	2026-05-20 13:47:49.486144
5	1	18	completed	30.00	\N	2026-04-29 12:00:22	2026-04-29 18:24:23.041782	2026-04-29 19:00:30.524218
87	24	18	in_progress	\N	\N	\N	2026-05-20 20:20:20.76163	2026-05-20 20:20:20.76163
88	34	45	in_progress	\N	\N	\N	2026-05-21 14:35:05.746627	2026-05-21 14:35:05.746627
89	34	46	in_progress	\N	\N	\N	2026-05-21 14:43:36.83788	2026-05-21 14:43:36.83788
90	24	46	in_progress	\N	\N	\N	2026-05-21 14:49:17.929766	2026-05-21 14:49:17.929766
91	24	45	in_progress	\N	\N	\N	2026-05-21 14:49:21.919036	2026-05-21 14:49:21.919036
92	34	47	in_progress	\N	\N	\N	2026-05-21 19:21:18.041352	2026-05-21 19:21:18.041352
97	24	61	in_progress	\N	\N	\N	2026-05-25 19:20:05.742981	2026-05-25 19:20:05.742981
98	34	61	in_progress	\N	\N	\N	2026-05-25 20:23:40.132786	2026-05-25 20:23:40.132786
99	24	1	in_progress	\N	\N	\N	2026-05-28 16:19:44.104329	2026-05-28 16:19:44.104329
100	34	38	in_progress	\N	\N	\N	2026-05-28 16:44:15.908577	2026-05-28 16:44:15.908577
101	1	12	in_progress	\N	\N	\N	2026-05-28 17:40:36.191082	2026-05-28 17:40:36.191082
102	1	37	in_progress	\N	\N	\N	2026-05-28 17:40:45.479265	2026-05-28 17:40:45.479265
103	1	23	in_progress	\N	\N	\N	2026-05-28 17:42:03.370168	2026-05-28 17:42:03.370168
105	5	23	in_progress	\N	\N	\N	2026-05-28 17:49:52.767464	2026-05-28 17:49:52.767464
106	5	1	in_progress	\N	\N	\N	2026-05-28 18:16:48.481583	2026-05-28 18:16:48.481583
22	2	1	completed	80.00	\N	2026-05-04 10:00:20	2026-05-04 16:59:48.126572	2026-05-04 17:00:26.135472
110	27	18	in_progress	\N	\N	\N	2026-05-29 00:35:30.863475	2026-05-29 00:35:30.863475
23	2	18	completed	80.00	\N	2026-05-04 10:28:40	2026-05-04 17:25:01.540394	2026-05-04 17:28:45.663924
24	2	9	in_progress	\N	\N	\N	2026-05-04 20:33:33.84878	2026-05-04 20:33:33.84878
111	27	61	in_progress	\N	\N	\N	2026-05-29 00:37:55.679644	2026-05-29 00:37:55.679644
69	32	22	completed	12.00	\N	2026-05-11 20:16:45	2026-05-11 20:15:29.957003	2026-05-11 20:16:46.083743
67	32	1	completed	80.00	\N	2026-05-11 20:17:59	2026-05-11 20:04:04.052771	2026-05-11 20:17:59.815096
112	27	62	in_progress	\N	\N	\N	2026-05-29 06:42:38.501447	2026-05-29 06:42:38.501447
113	27	63	in_progress	\N	\N	\N	2026-05-29 06:52:52.256195	2026-05-29 06:52:52.256195
114	34	23	in_progress	\N	\N	\N	2026-06-01 18:54:42.016118	2026-06-01 18:54:42.016118
115	34	60	in_progress	\N	\N	\N	2026-06-01 18:54:54.669047	2026-06-01 18:54:54.669047
30	14	1	in_progress	\N	\N	\N	2026-05-06 12:02:42.169279	2026-05-06 12:02:42.169279
31	13	2	in_progress	\N	\N	\N	2026-05-10 13:31:49.057815	2026-05-10 13:31:49.057815
32	15	1	completed	80.00	\N	2026-05-10 06:35:34	2026-05-10 13:34:15.199615	2026-05-10 13:35:38.433943
33	15	2	in_progress	\N	\N	\N	2026-05-11 14:47:28.310806	2026-05-11 14:47:28.310806
34	18	1	completed	100.00	\N	2026-05-11 16:06:50	2026-05-11 16:06:16.523432	2026-05-11 16:06:50.996888
35	19	1	completed	92.00	2026-05-11 19:51:59.749489	2026-05-11 19:51:59.749489	2026-05-11 19:51:59.749489	2026-05-11 19:51:59.749489
36	19	2	completed	85.00	2026-05-11 19:51:59.749489	2026-05-11 19:51:59.749489	2026-05-11 19:51:59.749489	2026-05-11 19:51:59.749489
37	20	1	completed	78.00	2026-05-11 19:52:03.821989	2026-05-11 19:52:03.821989	2026-05-11 19:52:03.821989	2026-05-11 19:52:03.821989
38	20	3	in_progress	\N	2026-05-11 19:52:03.821989	\N	2026-05-11 19:52:03.821989	2026-05-11 19:52:03.821989
39	21	1	completed	96.00	2026-05-11 19:52:09.698877	2026-05-11 19:52:09.698877	2026-05-11 19:52:09.698877	2026-05-11 19:52:09.698877
40	21	2	completed	94.00	2026-05-11 19:52:09.698877	2026-05-11 19:52:09.698877	2026-05-11 19:52:09.698877	2026-05-11 19:52:09.698877
41	21	4	completed	88.00	2026-05-11 19:52:09.698877	2026-05-11 19:52:09.698877	2026-05-11 19:52:09.698877	2026-05-11 19:52:09.698877
42	22	1	completed	89.00	2026-05-11 19:52:13.295563	2026-05-11 19:52:13.295563	2026-05-11 19:52:13.295563	2026-05-11 19:52:13.295563
43	22	3	completed	91.00	2026-05-11 19:52:13.295563	2026-05-11 19:52:13.295563	2026-05-11 19:52:13.295563	2026-05-11 19:52:13.295563
45	23	1	completed	100.00	2026-05-11 19:52:19.800342	2026-05-11 19:52:19.800342	2026-05-11 19:52:19.800342	2026-05-11 19:52:19.800342
46	23	2	completed	98.00	2026-05-11 19:52:19.800342	2026-05-11 19:52:19.800342	2026-05-11 19:52:19.800342	2026-05-11 19:52:19.800342
53	27	1	completed	87.00	2026-05-11 19:52:46.190151	2026-05-11 19:52:46.190151	2026-05-11 19:52:46.190151	2026-05-11 19:52:46.190151
54	27	2	completed	82.00	2026-05-11 19:52:46.190151	2026-05-11 19:52:46.190151	2026-05-11 19:52:46.190151	2026-05-11 19:52:46.190151
55	28	1	completed	79.00	2026-05-11 19:52:50.224026	2026-05-11 19:52:50.224026	2026-05-11 19:52:50.224026	2026-05-11 19:52:50.224026
56	28	3	completed	84.00	2026-05-11 19:52:50.224026	2026-05-11 19:52:50.224026	2026-05-11 19:52:50.224026	2026-05-11 19:52:50.224026
60	30	1	completed	100.00	2026-05-11 20:00:00.182857	2026-05-11 20:00:00.182857	2026-05-11 20:00:00.182857	2026-05-11 20:00:00.182857
61	30	2	completed	98.00	2026-05-11 20:00:00.182857	2026-05-11 20:00:00.182857	2026-05-11 20:00:00.182857	2026-05-11 20:00:00.182857
62	30	3	completed	96.00	2026-05-11 20:00:00.182857	2026-05-11 20:00:00.182857	2026-05-11 20:00:00.182857	2026-05-11 20:00:00.182857
63	30	4	in_progress	\N	2026-05-11 20:00:00.182857	\N	2026-05-11 20:00:00.182857	2026-05-11 20:00:00.182857
64	31	1	completed	88.00	2026-05-11 20:00:04.823229	2026-05-11 20:00:04.823229	2026-05-11 20:00:04.823229	2026-05-11 20:00:04.823229
65	31	2	completed	85.00	2026-05-11 20:00:04.823229	2026-05-11 20:00:04.823229	2026-05-11 20:00:04.823229	2026-05-11 20:00:04.823229
66	31	4	in_progress	\N	2026-05-11 20:00:04.823229	\N	2026-05-11 20:00:04.823229	2026-05-11 20:00:04.823229
57	29	1	completed	100.00	2026-05-11 19:59:55.616934	2026-05-11 19:59:55.616934	2026-05-11 19:59:55.616934	2026-05-11 19:59:55.616934
29	13	18	completed	90.00	\N	2026-05-06 11:36:16	2026-05-06 09:32:41.109383	2026-05-06 11:36:17.170172
25	13	1	completed	100.00	\N	2026-05-06 01:00:45	2026-05-06 00:50:03.676503	2026-05-06 01:00:45.803494
76	29	18	completed	80.00	\N	2026-05-11 21:24:25	2026-05-11 21:15:49.80382	2026-05-11 21:24:25.788411
77	29	23	in_progress	\N	\N	\N	2026-05-11 21:28:08.464802	2026-05-11 21:28:08.464802
79	5	18	completed	80.00	\N	2026-05-11 21:36:23	2026-05-11 21:35:58.097752	2026-05-11 21:36:24.44365
80	32	18	completed	60.00	\N	2026-05-11 22:17:01	2026-05-11 22:16:43.251295	2026-05-11 22:17:02.30129
\.


--
-- TOC entry 3555 (class 0 OID 16540)
-- Dependencies: 231
-- Data for Name: user_lesson_progress; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.user_lesson_progress (id, user_id, lesson_id, is_completed, completed_at, score, updated_at) FROM stdin;
1	1	6	t	2026-04-29 18:43:45.366758	\N	2026-04-29 18:43:45.366758
2	1	7	t	2026-04-29 18:49:53.774047	\N	2026-04-29 18:49:53.774047
3	1	8	t	2026-04-29 18:50:20.749162	\N	2026-04-29 18:50:20.749162
4	1	9	t	2026-04-29 18:50:28.037314	\N	2026-04-29 18:50:28.037314
5	1	10	t	2026-04-29 18:50:34.05015	\N	2026-04-29 18:50:34.05015
163	27	6	t	2026-05-29 00:35:43.648703	\N	2026-05-29 00:35:43.648703
170	34	10	t	2026-06-01 19:09:27.437665	\N	2026-06-01 19:09:27.437665
171	34	9	t	2026-06-01 19:09:30.358139	\N	2026-06-01 19:09:30.358139
172	34	8	t	2026-06-01 19:09:35.331167	\N	2026-06-01 19:09:35.331167
173	34	7	t	2026-06-01 19:09:42.458255	\N	2026-06-01 19:09:42.458255
50	2	2	t	2026-05-04 16:59:55.123229	\N	2026-05-04 16:59:55.123229
51	2	3	t	2026-05-04 16:59:57.793175	\N	2026-05-04 16:59:57.793175
52	2	4	t	2026-05-04 16:59:59.895354	\N	2026-05-04 16:59:59.895354
53	2	5	t	2026-05-04 17:00:02.66199	\N	2026-05-04 17:00:02.66199
54	2	1	t	2026-05-04 17:26:01.505312	\N	2026-05-04 17:26:01.505312
55	2	6	t	2026-05-04 17:26:23.718613	\N	2026-05-04 17:26:23.718613
56	2	7	t	2026-05-04 17:26:26.65609	\N	2026-05-04 17:26:26.65609
57	2	8	t	2026-05-04 17:26:29.596756	\N	2026-05-04 17:26:29.596756
58	2	9	t	2026-05-04 17:26:32.664083	\N	2026-05-04 17:26:32.664083
59	2	10	t	2026-05-04 17:26:36.666711	\N	2026-05-04 17:26:36.666711
60	13	1	t	2026-05-06 00:50:58.520019	\N	2026-05-06 00:50:58.520019
61	13	2	t	2026-05-06 00:51:07.934555	\N	2026-05-06 00:51:07.934555
62	13	3	t	2026-05-06 00:51:10.873783	\N	2026-05-06 00:51:10.873783
63	13	4	t	2026-05-06 00:51:13.02415	\N	2026-05-06 00:51:13.02415
64	13	5	t	2026-05-06 00:51:15.288753	\N	2026-05-06 00:51:15.288753
65	13	6	t	2026-05-06 01:10:20.363648	\N	2026-05-06 01:10:20.363648
66	13	7	t	2026-05-06 11:35:18.560135	\N	2026-05-06 11:35:18.560135
67	13	8	t	2026-05-06 11:35:20.69292	\N	2026-05-06 11:35:20.69292
68	13	9	t	2026-05-06 11:35:23.363588	\N	2026-05-06 11:35:23.363588
69	13	10	t	2026-05-06 11:35:25.170216	\N	2026-05-06 11:35:25.170216
70	14	1	t	2026-05-06 12:03:15.44482	\N	2026-05-06 12:03:15.44482
71	15	1	t	2026-05-10 13:34:25.79122	\N	2026-05-10 13:34:25.79122
72	15	2	t	2026-05-10 13:34:55.353636	\N	2026-05-10 13:34:55.353636
73	15	3	t	2026-05-10 13:34:58.366934	\N	2026-05-10 13:34:58.366934
74	15	5	t	2026-05-10 13:35:00.624596	\N	2026-05-10 13:35:00.624596
75	15	4	t	2026-05-10 13:35:03.186632	\N	2026-05-10 13:35:03.186632
76	18	1	t	2026-05-11 16:06:18.451773	\N	2026-05-11 16:06:18.451773
78	18	3	t	2026-05-11 16:06:22.401049	\N	2026-05-11 16:06:22.401049
79	18	2	t	2026-05-11 16:06:28.838223	\N	2026-05-11 16:06:28.838223
80	18	4	t	2026-05-11 16:06:32.138073	\N	2026-05-11 16:06:32.138073
81	18	5	t	2026-05-11 16:06:34.662272	\N	2026-05-11 16:06:34.662272
82	19	1	t	2026-05-11 19:53:22.091541	\N	2026-05-11 19:53:22.091541
83	19	2	t	2026-05-11 19:53:22.091541	\N	2026-05-11 19:53:22.091541
84	19	3	t	2026-05-11 19:53:22.091541	\N	2026-05-11 19:53:22.091541
85	20	1	t	2026-05-11 19:53:22.091541	\N	2026-05-11 19:53:22.091541
86	20	2	t	2026-05-11 19:53:22.091541	\N	2026-05-11 19:53:22.091541
87	21	1	t	2026-05-11 19:53:32.689223	\N	2026-05-11 19:53:32.689223
88	21	2	t	2026-05-11 19:53:32.689223	\N	2026-05-11 19:53:32.689223
89	21	3	t	2026-05-11 19:53:32.689223	\N	2026-05-11 19:53:32.689223
90	22	1	t	2026-05-11 19:53:32.689223	\N	2026-05-11 19:53:32.689223
91	22	2	t	2026-05-11 19:53:32.689223	\N	2026-05-11 19:53:32.689223
92	23	1	t	2026-05-11 19:53:38.416706	\N	2026-05-11 19:53:38.416706
93	23	2	t	2026-05-11 19:53:38.416706	\N	2026-05-11 19:53:38.416706
94	23	3	t	2026-05-11 19:53:38.416706	\N	2026-05-11 19:53:38.416706
95	24	1	t	2026-05-11 19:53:38.416706	\N	2026-05-11 19:53:38.416706
96	24	2	t	2026-05-11 19:53:38.416706	\N	2026-05-11 19:53:38.416706
97	25	1	t	2026-05-11 19:53:42.490871	\N	2026-05-11 19:53:42.490871
98	25	2	t	2026-05-11 19:53:42.490871	\N	2026-05-11 19:53:42.490871
99	25	3	t	2026-05-11 19:53:42.490871	\N	2026-05-11 19:53:42.490871
100	26	1	t	2026-05-11 19:53:42.490871	\N	2026-05-11 19:53:42.490871
101	26	2	t	2026-05-11 19:53:42.490871	\N	2026-05-11 19:53:42.490871
102	27	1	t	2026-05-11 19:53:46.64624	\N	2026-05-11 19:53:46.64624
103	27	2	t	2026-05-11 19:53:46.64624	\N	2026-05-11 19:53:46.64624
104	28	1	t	2026-05-11 19:53:46.64624	\N	2026-05-11 19:53:46.64624
105	28	2	t	2026-05-11 19:53:46.64624	\N	2026-05-11 19:53:46.64624
106	32	11	t	2026-05-11 20:15:39.845402	\N	2026-05-11 20:15:39.845402
107	32	12	t	2026-05-11 20:15:41.349259	\N	2026-05-11 20:15:41.349259
108	32	13	t	2026-05-11 20:15:42.859612	\N	2026-05-11 20:15:42.859612
110	32	2	t	2026-05-11 20:17:29.798547	\N	2026-05-11 20:17:29.798547
111	32	3	t	2026-05-11 20:17:31.5333	\N	2026-05-11 20:17:31.5333
113	32	4	t	2026-05-11 20:17:35.497298	\N	2026-05-11 20:17:35.497298
114	32	5	t	2026-05-11 20:17:37.452855	\N	2026-05-11 20:17:37.452855
115	32	1	t	2026-05-11 20:17:41.193109	\N	2026-05-11 20:17:41.193109
116	32	6	t	2026-05-11 20:18:21.718279	\N	2026-05-11 20:18:21.718279
117	32	7	t	2026-05-11 20:18:24.284025	\N	2026-05-11 20:18:24.284025
120	32	10	t	2026-05-11 20:18:32.486388	\N	2026-05-11 20:18:32.486388
121	32	9	t	2026-05-11 20:18:36.367262	\N	2026-05-11 20:18:36.367262
118	32	8	t	2026-05-11 20:18:39.334763	\N	2026-05-11 20:18:39.334763
137	29	7	t	2026-05-11 21:23:19.965324	\N	2026-05-11 21:23:19.965324
138	29	8	t	2026-05-11 21:23:22.44927	\N	2026-05-11 21:23:22.44927
140	29	9	t	2026-05-11 21:23:41.247723	\N	2026-05-11 21:23:41.247723
141	29	10	t	2026-05-11 21:23:47.140333	\N	2026-05-11 21:23:47.140333
142	29	6	t	2026-05-11 21:23:49.822138	\N	2026-05-11 21:23:49.822138
144	5	7	t	2026-05-11 21:33:25.499707	\N	2026-05-11 21:33:25.499707
145	5	8	t	2026-05-11 21:33:28.216802	\N	2026-05-11 21:33:28.216802
146	5	9	t	2026-05-11 21:33:32.971314	\N	2026-05-11 21:33:32.971314
147	5	10	t	2026-05-11 21:33:37.822814	\N	2026-05-11 21:33:37.822814
148	5	6	t	2026-05-11 21:33:43.070281	\N	2026-05-11 21:33:43.070281
150	24	25	t	2026-05-12 20:49:28.455183	\N	2026-05-12 20:49:28.455183
151	24	26	t	2026-05-12 20:49:31.720488	\N	2026-05-12 20:49:31.720488
152	24	27	t	2026-05-12 20:49:44.083722	\N	2026-05-12 20:49:44.083722
153	34	1	t	2026-05-12 22:44:53.33077	\N	2026-05-12 22:44:53.33077
154	34	2	t	2026-05-12 22:44:55.333206	\N	2026-05-12 22:44:55.333206
155	34	3	t	2026-05-12 22:44:57.25625	\N	2026-05-12 22:44:57.25625
156	34	4	t	2026-05-12 22:44:59.08814	\N	2026-05-12 22:44:59.08814
157	34	5	t	2026-05-12 22:45:00.885263	\N	2026-05-12 22:45:00.885263
158	34	6	t	2026-05-14 22:03:03.030909	\N	2026-05-14 22:03:03.030909
\.


--
-- TOC entry 3553 (class 0 OID 16520)
-- Dependencies: 229
-- Data for Name: user_test_results; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.user_test_results (id, user_id, test_id, score, total_questions, percentage, passed, completed_at) FROM stdin;
1	1	2	3	10	30	f	2026-04-29 19:00:30.356052
69	29	2	8	10	80	t	2026-05-11 21:24:25.549609
71	5	2	6	10	80	t	2026-05-11 21:36:24.199628
73	32	2	6	10	60	t	2026-05-11 22:17:02.169705
75	24	18	3	3	100	t	2026-05-12 20:51:05.837673
76	34	1	4	5	80	t	2026-05-12 22:45:18.176307
13	5	1	8	10	90	t	2026-05-03 15:33:11.812019
11	2	1	4	5	80	t	2026-05-04 17:00:25.952976
12	2	2	8	10	80	t	2026-05-04 17:28:45.492488
23	15	1	4	5	80	t	2026-05-10 13:35:38.279962
24	18	1	5	5	100	t	2026-05-11 16:06:50.87739
25	19	1	9	10	90	t	2026-05-11 19:52:57.859554
26	19	2	8	10	80	t	2026-05-11 19:52:57.859554
27	20	1	7	10	70	t	2026-05-11 19:52:57.871161
33	23	1	10	10	100	t	2026-05-11 19:52:57.92776
34	23	2	10	10	100	t	2026-05-11 19:52:57.92776
41	27	1	8	10	85	t	2026-05-11 19:52:57.974145
42	27	2	8	10	80	t	2026-05-11 19:52:57.974145
59	30	1	10	10	100	t	2026-05-11 20:02:26.81193
60	30	2	10	10	100	t	2026-05-11 20:02:26.81193
61	31	1	8	10	80	t	2026-05-11 20:02:30.932322
62	31	2	8	10	80	t	2026-05-11 20:02:30.932322
63	32	3	1	8	12	f	2026-05-11 20:16:45.809257
64	32	1	4	5	80	t	2026-05-11 20:17:59.560758
66	29	1	3	5	100	t	2026-05-11 21:19:36.441067
22	13	2	9	10	90	t	2026-05-06 11:36:17.059212
16	13	1	5	5	100	t	2026-05-06 01:00:45.669445
\.


--
-- TOC entry 3541 (class 0 OID 16409)
-- Dependencies: 216
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (id, email, password, first_name, last_name, country, user_type, university, created_at) FROM stdin;
3	ekaterina.kuznetsova@example.com	password123	Екатерина	Кузнецова	Россия	student	НГУ	2026-04-28 21:15:54.349365
4	max@yandex.ru	max123	Максим	Грищенко	Россия	student	НГТУ	2026-04-28 21:15:54.349365
1	maria.ivanova@example.com	111111	Мария	Иванова	Россия	teacher	СПбГУ	2026-04-28 21:15:54.349365
13	lesha@gmail.com	lesha1	Лёша	Руков	Россия	student	МГУ	2026-05-06 00:49:43.897129
14	iva@gmail.com	ivan11	Иван	Гаврилов	Китай	student	Чинчанчу	2026-05-06 12:02:08.710417
15	zah@gmail.com	zah111	захар	рожков	Бразилия	student	БразГУ	2026-05-10 13:33:49.340544
16	ura@gmail.com	ura111	Юра	Прохор	Россия	student	Нгау	2026-05-10 15:30:14.741399
18	ali@gmail.com	ali111	Али	Грунди	Индия	student	INDG	2026-05-11 16:06:00.29894
19	joao.silva@usp.br	pass123	Жоао	Сильва	Бразилия	student	Университет Сан-Паулу	2026-05-11 19:49:18.787251
20	maria.santos@ufrj.br	pass123	Мария	Сантос	Бразилия	student	Университет Рио-де-Жанейро	2026-05-11 19:49:18.787251
21	dmitry.ivanov@msu.ru	pass123	Дмитрий	Иванов	Россия	student	Московский государственный университет	2026-05-11 19:49:18.787251
22	elena.petrova@spbu.ru	pass123	Елена	Петрова	Россия	student	Санкт-Петербургский университет	2026-05-11 19:49:18.787251
23	raj.kumar@iitb.ac.in	pass123	Радж	Кумар	Индия	student	Индийский технологический институт	2026-05-11 19:49:18.787251
24	priya.sharma@iitd.ac.in	pass123	Прия	Шарма	Индия	teacher	Индийский технологический институт Дели	2026-05-11 19:49:18.787251
25	wei.zhang@tsinghua.edu.cn	pass123	Вэй	Чжан	Китай	student	Университет Цинхуа	2026-05-11 19:49:18.787251
26	hong.liu@zju.edu.cn	pass123	Хун	Лю	Китай	teacher	Чжэцзянский университет	2026-05-11 19:49:18.787251
28	sarah.johnson@wits.ac.za	pass123	Сара	Джонсон	Южная Африка	student	Университет Витватерсранда	2026-05-11 19:49:18.787251
30	jian.li@fudan.edu.cn	pass123	Цзянь	Ли	Китай	student	Университет Фудань	2026-05-11 19:58:19.751426
31	zanele.dlamini@ukzn.ac.za	pass123	Занеле	Дламини	Южная Африка	student	Университет Квазулу-Натал	2026-05-11 19:58:19.772642
29	maksim.volkov@ya.ru	pass123	Максим	Волков	Россия	student	Новосибирский государственный технический университет	2026-05-11 19:58:14.320233
2	ivan.petrov@example.com	111111	Иван	Петров	Россия	admin	Пекинский университет	2026-04-28 21:15:54.349365
32	t@gmail.com	tttttt	Жак	Руби	Иран	student	ИРГУ	2026-05-11 20:03:52.230883
5	dmitry.sokolov@example.com	password123	Дмитрий	Соколов	Бразилия	teacher	Университет Сан-Паулу	2026-04-28 21:15:54.349365
34	dav@gmail.com	dddddd	Вей	Ли	Китай	teacher	КГУ	2026-05-12 22:37:53.351486
27	thabo.ndlovu@uct.ac.za	pass123	Табо	Ндлову	Южная Африка	teacher	Кейптаунский университет	2026-05-11 19:49:18.787251
\.


--
-- TOC entry 3597 (class 0 OID 0)
-- Dependencies: 233
-- Name: course_contributions_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.course_contributions_id_seq', 5, true);


--
-- TOC entry 3598 (class 0 OID 0)
-- Dependencies: 235
-- Name: course_suggestions_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.course_suggestions_id_seq', 8, true);


--
-- TOC entry 3599 (class 0 OID 0)
-- Dependencies: 224
-- Name: course_tests_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.course_tests_id_seq', 31, true);


--
-- TOC entry 3600 (class 0 OID 0)
-- Dependencies: 217
-- Name: courses_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.courses_id_seq', 72, true);


--
-- TOC entry 3601 (class 0 OID 0)
-- Dependencies: 222
-- Name: lessons_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.lessons_id_seq', 197, true);


--
-- TOC entry 3602 (class 0 OID 0)
-- Dependencies: 226
-- Name: test_questions_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.test_questions_id_seq', 55, true);


--
-- TOC entry 3603 (class 0 OID 0)
-- Dependencies: 220
-- Name: user_courses_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.user_courses_id_seq', 115, true);


--
-- TOC entry 3604 (class 0 OID 0)
-- Dependencies: 230
-- Name: user_lesson_progress_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.user_lesson_progress_id_seq', 173, true);


--
-- TOC entry 3605 (class 0 OID 0)
-- Dependencies: 228
-- Name: user_test_results_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.user_test_results_id_seq', 78, true);


--
-- TOC entry 3606 (class 0 OID 0)
-- Dependencies: 215
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.users_id_seq', 34, true);


--
-- TOC entry 3371 (class 2606 OID 16585)
-- Name: course_contributions course_contributions_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.course_contributions
    ADD CONSTRAINT course_contributions_pkey PRIMARY KEY (id);


--
-- TOC entry 3375 (class 2606 OID 16619)
-- Name: course_suggestions course_suggestions_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.course_suggestions
    ADD CONSTRAINT course_suggestions_pkey PRIMARY KEY (id);


--
-- TOC entry 3356 (class 2606 OID 16496)
-- Name: course_tests course_tests_course_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.course_tests
    ADD CONSTRAINT course_tests_course_id_key UNIQUE (course_id);


--
-- TOC entry 3358 (class 2606 OID 16494)
-- Name: course_tests course_tests_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.course_tests
    ADD CONSTRAINT course_tests_pkey PRIMARY KEY (id);


--
-- TOC entry 3344 (class 2606 OID 16431)
-- Name: courses courses_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.courses
    ADD CONSTRAINT courses_pkey PRIMARY KEY (id);


--
-- TOC entry 3354 (class 2606 OID 16479)
-- Name: lessons lessons_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.lessons
    ADD CONSTRAINT lessons_pkey PRIMARY KEY (id);


--
-- TOC entry 3360 (class 2606 OID 16513)
-- Name: test_questions test_questions_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.test_questions
    ADD CONSTRAINT test_questions_pkey PRIMARY KEY (id);


--
-- TOC entry 3349 (class 2606 OID 16453)
-- Name: user_courses user_courses_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_courses
    ADD CONSTRAINT user_courses_pkey PRIMARY KEY (id);


--
-- TOC entry 3351 (class 2606 OID 16455)
-- Name: user_courses user_courses_user_id_course_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_courses
    ADD CONSTRAINT user_courses_user_id_course_id_key UNIQUE (user_id, course_id);


--
-- TOC entry 3367 (class 2606 OID 16547)
-- Name: user_lesson_progress user_lesson_progress_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_lesson_progress
    ADD CONSTRAINT user_lesson_progress_pkey PRIMARY KEY (id);


--
-- TOC entry 3369 (class 2606 OID 16549)
-- Name: user_lesson_progress user_lesson_progress_user_id_lesson_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_lesson_progress
    ADD CONSTRAINT user_lesson_progress_user_id_lesson_id_key UNIQUE (user_id, lesson_id);


--
-- TOC entry 3362 (class 2606 OID 16526)
-- Name: user_test_results user_test_results_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_test_results
    ADD CONSTRAINT user_test_results_pkey PRIMARY KEY (id);


--
-- TOC entry 3364 (class 2606 OID 16528)
-- Name: user_test_results user_test_results_user_id_test_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_test_results
    ADD CONSTRAINT user_test_results_user_id_test_id_key UNIQUE (user_id, test_id);


--
-- TOC entry 3340 (class 2606 OID 16419)
-- Name: users users_email_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_email_key UNIQUE (email);


--
-- TOC entry 3342 (class 2606 OID 16417)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- TOC entry 3372 (class 1259 OID 16606)
-- Name: idx_contributions_course; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_contributions_course ON public.course_contributions USING btree (course_id);


--
-- TOC entry 3373 (class 1259 OID 16607)
-- Name: idx_contributions_lesson; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_contributions_lesson ON public.course_contributions USING btree (lesson_id);


--
-- TOC entry 3352 (class 1259 OID 16634)
-- Name: idx_lessons_is_brics; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_lessons_is_brics ON public.lessons USING btree (is_brics);


--
-- TOC entry 3376 (class 1259 OID 16630)
-- Name: idx_suggestions_course; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_suggestions_course ON public.course_suggestions USING btree (course_id);


--
-- TOC entry 3377 (class 1259 OID 16631)
-- Name: idx_suggestions_status; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_suggestions_status ON public.course_suggestions USING btree (status);


--
-- TOC entry 3378 (class 1259 OID 16632)
-- Name: idx_suggestions_suggested_by; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_suggestions_suggested_by ON public.course_suggestions USING btree (suggested_by);


--
-- TOC entry 3345 (class 1259 OID 16467)
-- Name: idx_user_courses_course_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_user_courses_course_id ON public.user_courses USING btree (course_id);


--
-- TOC entry 3346 (class 1259 OID 16468)
-- Name: idx_user_courses_status; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_user_courses_status ON public.user_courses USING btree (status);


--
-- TOC entry 3347 (class 1259 OID 16466)
-- Name: idx_user_courses_user_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_user_courses_user_id ON public.user_courses USING btree (user_id);


--
-- TOC entry 3365 (class 1259 OID 16560)
-- Name: idx_user_lesson_progress_user; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_user_lesson_progress_user ON public.user_lesson_progress USING btree (user_id);


--
-- TOC entry 3389 (class 2606 OID 16601)
-- Name: course_contributions course_contributions_approved_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.course_contributions
    ADD CONSTRAINT course_contributions_approved_by_fkey FOREIGN KEY (approved_by) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 3390 (class 2606 OID 16596)
-- Name: course_contributions course_contributions_contributor_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.course_contributions
    ADD CONSTRAINT course_contributions_contributor_id_fkey FOREIGN KEY (contributor_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 3391 (class 2606 OID 16586)
-- Name: course_contributions course_contributions_course_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.course_contributions
    ADD CONSTRAINT course_contributions_course_id_fkey FOREIGN KEY (course_id) REFERENCES public.courses(id) ON DELETE CASCADE;


--
-- TOC entry 3392 (class 2606 OID 16591)
-- Name: course_contributions course_contributions_lesson_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.course_contributions
    ADD CONSTRAINT course_contributions_lesson_id_fkey FOREIGN KEY (lesson_id) REFERENCES public.lessons(id) ON DELETE CASCADE;


--
-- TOC entry 3393 (class 2606 OID 16620)
-- Name: course_suggestions course_suggestions_course_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.course_suggestions
    ADD CONSTRAINT course_suggestions_course_id_fkey FOREIGN KEY (course_id) REFERENCES public.courses(id) ON DELETE CASCADE;


--
-- TOC entry 3394 (class 2606 OID 16625)
-- Name: course_suggestions course_suggestions_suggested_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.course_suggestions
    ADD CONSTRAINT course_suggestions_suggested_by_fkey FOREIGN KEY (suggested_by) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 3383 (class 2606 OID 16497)
-- Name: course_tests course_tests_course_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.course_tests
    ADD CONSTRAINT course_tests_course_id_fkey FOREIGN KEY (course_id) REFERENCES public.courses(id) ON DELETE CASCADE;


--
-- TOC entry 3379 (class 2606 OID 16432)
-- Name: courses courses_creator_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.courses
    ADD CONSTRAINT courses_creator_id_fkey FOREIGN KEY (creator_id) REFERENCES public.users(id) ON DELETE SET NULL;


--
-- TOC entry 3382 (class 2606 OID 16480)
-- Name: lessons lessons_course_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.lessons
    ADD CONSTRAINT lessons_course_id_fkey FOREIGN KEY (course_id) REFERENCES public.courses(id) ON DELETE CASCADE;


--
-- TOC entry 3384 (class 2606 OID 16514)
-- Name: test_questions test_questions_test_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.test_questions
    ADD CONSTRAINT test_questions_test_id_fkey FOREIGN KEY (test_id) REFERENCES public.course_tests(id) ON DELETE CASCADE;


--
-- TOC entry 3380 (class 2606 OID 16461)
-- Name: user_courses user_courses_course_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_courses
    ADD CONSTRAINT user_courses_course_id_fkey FOREIGN KEY (course_id) REFERENCES public.courses(id) ON DELETE CASCADE;


--
-- TOC entry 3381 (class 2606 OID 16456)
-- Name: user_courses user_courses_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_courses
    ADD CONSTRAINT user_courses_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 3387 (class 2606 OID 16555)
-- Name: user_lesson_progress user_lesson_progress_lesson_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_lesson_progress
    ADD CONSTRAINT user_lesson_progress_lesson_id_fkey FOREIGN KEY (lesson_id) REFERENCES public.lessons(id) ON DELETE CASCADE;


--
-- TOC entry 3388 (class 2606 OID 16550)
-- Name: user_lesson_progress user_lesson_progress_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_lesson_progress
    ADD CONSTRAINT user_lesson_progress_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 3385 (class 2606 OID 16534)
-- Name: user_test_results user_test_results_test_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_test_results
    ADD CONSTRAINT user_test_results_test_id_fkey FOREIGN KEY (test_id) REFERENCES public.course_tests(id);


--
-- TOC entry 3386 (class 2606 OID 16562)
-- Name: user_test_results user_test_results_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_test_results
    ADD CONSTRAINT user_test_results_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 3565 (class 0 OID 0)
-- Dependencies: 5
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: pg_database_owner
--

GRANT ALL ON SCHEMA public TO app_user;


--
-- TOC entry 3566 (class 0 OID 0)
-- Dependencies: 229
-- Name: TABLE user_test_results; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.user_test_results TO app_user;


--
-- TOC entry 3567 (class 0 OID 0)
-- Dependencies: 216
-- Name: TABLE users; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.users TO app_user;


--
-- TOC entry 3568 (class 0 OID 0)
-- Dependencies: 234
-- Name: TABLE course_contributions; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.course_contributions TO app_user;


--
-- TOC entry 3570 (class 0 OID 0)
-- Dependencies: 233
-- Name: SEQUENCE course_contributions_id_seq; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT,USAGE ON SEQUENCE public.course_contributions_id_seq TO app_user;


--
-- TOC entry 3571 (class 0 OID 0)
-- Dependencies: 236
-- Name: TABLE course_suggestions; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.course_suggestions TO app_user;


--
-- TOC entry 3573 (class 0 OID 0)
-- Dependencies: 235
-- Name: SEQUENCE course_suggestions_id_seq; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT,USAGE ON SEQUENCE public.course_suggestions_id_seq TO app_user;


--
-- TOC entry 3574 (class 0 OID 0)
-- Dependencies: 225
-- Name: TABLE course_tests; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.course_tests TO app_user;


--
-- TOC entry 3576 (class 0 OID 0)
-- Dependencies: 224
-- Name: SEQUENCE course_tests_id_seq; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON SEQUENCE public.course_tests_id_seq TO app_user;


--
-- TOC entry 3577 (class 0 OID 0)
-- Dependencies: 218
-- Name: TABLE courses; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.courses TO app_user;


--
-- TOC entry 3579 (class 0 OID 0)
-- Dependencies: 217
-- Name: SEQUENCE courses_id_seq; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON SEQUENCE public.courses_id_seq TO app_user;


--
-- TOC entry 3580 (class 0 OID 0)
-- Dependencies: 219
-- Name: TABLE courses_with_creators; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.courses_with_creators TO app_user;


--
-- TOC entry 3581 (class 0 OID 0)
-- Dependencies: 223
-- Name: TABLE lessons; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.lessons TO app_user;


--
-- TOC entry 3583 (class 0 OID 0)
-- Dependencies: 222
-- Name: SEQUENCE lessons_id_seq; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON SEQUENCE public.lessons_id_seq TO app_user;


--
-- TOC entry 3584 (class 0 OID 0)
-- Dependencies: 227
-- Name: TABLE test_questions; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.test_questions TO app_user;


--
-- TOC entry 3586 (class 0 OID 0)
-- Dependencies: 226
-- Name: SEQUENCE test_questions_id_seq; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON SEQUENCE public.test_questions_id_seq TO app_user;


--
-- TOC entry 3587 (class 0 OID 0)
-- Dependencies: 221
-- Name: TABLE user_courses; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.user_courses TO app_user;


--
-- TOC entry 3589 (class 0 OID 0)
-- Dependencies: 220
-- Name: SEQUENCE user_courses_id_seq; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON SEQUENCE public.user_courses_id_seq TO app_user;


--
-- TOC entry 3590 (class 0 OID 0)
-- Dependencies: 231
-- Name: TABLE user_lesson_progress; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON TABLE public.user_lesson_progress TO app_user;


--
-- TOC entry 3592 (class 0 OID 0)
-- Dependencies: 230
-- Name: SEQUENCE user_lesson_progress_id_seq; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON SEQUENCE public.user_lesson_progress_id_seq TO app_user;


--
-- TOC entry 3594 (class 0 OID 0)
-- Dependencies: 228
-- Name: SEQUENCE user_test_results_id_seq; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON SEQUENCE public.user_test_results_id_seq TO app_user;


--
-- TOC entry 3596 (class 0 OID 0)
-- Dependencies: 215
-- Name: SEQUENCE users_id_seq; Type: ACL; Schema: public; Owner: postgres
--

GRANT ALL ON SEQUENCE public.users_id_seq TO app_user;


--
-- TOC entry 2091 (class 826 OID 16390)
-- Name: DEFAULT PRIVILEGES FOR TABLES; Type: DEFAULT ACL; Schema: public; Owner: postgres
--

ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA public GRANT ALL ON TABLES TO app_user;


-- Completed on 2026-06-10 17:36:43

--
-- PostgreSQL database dump complete
--

\unrestrict UuexPRi2FvvrkPW0fg2Cge1iIZzNxofJOHFKZdqwMcjHwHBCFx2wkyBvaKKazID

