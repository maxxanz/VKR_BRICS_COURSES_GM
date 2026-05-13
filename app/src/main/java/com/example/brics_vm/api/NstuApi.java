package com.example.brics_vm.api;

import com.example.brics_vm.models.CountryRanking;
import com.example.brics_vm.models.Course;
import com.example.brics_vm.models.TestResponse;
import com.example.brics_vm.models.User;
import com.example.brics_vm.models.UserCourse;
import com.example.brics_vm.models.Lesson;
import com.example.brics_vm.models.TestQuestion;
import com.example.brics_vm.models.TestResult;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;
import java.util.Map;

public interface NstuApi {

    // ========== ПОЛЬЗОВАТЕЛИ ==========

    @POST("rest/v1/Users")
    Call<User> registerUser(@Body User user);

    @GET("rest/v1/Users")
    Call<List<User>> checkEmailExists(@Query("email") String email);

    @GET("rest/v1/Users")
    Call<List<User>> loginUser(@Query("email") String email);

    @GET("rest/v1/Users")
    Call<List<User>> getAllUsers();

    // ========== КУРСЫ ==========

    // Получить все курсы
    // Используйте view вместо таблицы courses
    @GET("rest/v1/courses_with_creators")
    Call<List<Course>> getAllCourses();

    // Получить курс по ID
    @GET("rest/v1/courses")
    Call<List<Course>> getCourseById(@Query("id") int id);

    // Получить курсы по уровню (course_number)
    @GET("rest/v1/courses")
    Call<List<Course>> getCoursesByLevel(@Query("course_number") int courseNumber);

    // Получить курсы по предмету
    @GET("rest/v1/courses")
    Call<List<Course>> getCoursesBySubject(@Query("subject") String subject);

    // Получить курсы пользователя (с деталями курса)
    @GET("rest/v1/user_courses?select=*,courses(*)")
    Call<List<UserCourse>> getUserCoursesWithDetails(@Query("user_id") int userId);

    // Добавить курс в избранное
    @POST("rest/v1/user_courses")
    Call<UserCourse> addToFavorites(@Body UserCourse userCourse);

    // Обновить статус курса
    @PATCH("rest/v1/user_courses")
    Call<Void> updateCourseStatus(
            @Query("id") int enrollmentId,
            @Body Map<String, Object> updates
    );

    // Удалить из избранного
    @DELETE("rest/v1/user_courses")
    Call<Void> removeFromFavorites(@Query("id") int enrollmentId);

    // Получить уроки курса
    @GET("rest/v1/lessons")
    Call<List<Lesson>> getCourseLessons(@Query("course_id") int courseId);

    // Проверить, пройден ли урок
    @GET("rest/v1/user_lesson_progress")
    Call<List<Integer>> getUserLessonProgress(
            @Query("user_id") int userId,
            @Query("course_id") int courseId
    );

    // Отметить урок пройденным
    @POST("rest/v1/lessons/{lesson_id}/complete")
    Call<Void> completeLesson(
            @Path("lesson_id") int lessonId,
            @Query("user_id") int userId
    );

    // Получить вопросы теста
    @GET("rest/v1/test_questions")
    Call<List<TestQuestion>> getTestQuestions(@Query("test_id") int testId);

    // Сохранить результат теста
    @POST("rest/v1/user_test_results")
    Call<Void> submitTestResult(@Body TestResult result);

    // Обновить результат в user_courses
    @PATCH("rest/v1/user_courses")
    Call<Void> updateCourseResult(
            @Query("user_id") int userId,
            @Query("course_id") int courseId,
            @Query("result") int result,
            @Query("status") String status,
            @Query("completed_at") String completedAt
    );

    @GET("rest/v1/course_tests")
    Call<Integer> getCourseTest(@Query("course_id") int courseId);;

    @GET("rest/v1/user_courses")
    Call<List<UserCourse>> getUserCourseStatus(
            @Query("user_id") int userId,
            @Query("course_id") int courseId
    );

    @DELETE("rest/v1/lessons/{lesson_id}/progress")
    Call<Void> uncompleteLesson(
            @Path("lesson_id") int lessonId,
            @Query("user_id") int userId
    );

    // Получить рейтинг стран из VIEW
    @GET("rest/v1/country_ranking")
    Call<List<CountryRanking>> getCountryRanking();

    // С сортировкой по рангу
    @GET("rest/v1/country_ranking?order=rank.asc")
    Call<List<CountryRanking>> getCountryRankingOrdered();

    // Создание курса
    @POST("rest/v1/courses")
    Call<Course> createCourse(@Body Course course);

    // Создание урока
    @POST("rest/v1/lessons")
    Call<Lesson> createLesson(@Body Lesson lesson);

    // Создание теста для курса
    @POST("/rest/v1/course_tests")
    Call<TestResponse> createCourseTest(@Query("course_id") int courseId);

    // Добавление вопроса
    @POST("rest/v1/test_questions")
    Call<TestQuestion> createQuestion(@Body TestQuestion question);


}