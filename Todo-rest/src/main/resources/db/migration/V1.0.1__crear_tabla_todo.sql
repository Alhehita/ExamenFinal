CREATE TABLE IF NOT EXISTS public.todos
(
    id        SERIAL PRIMARY KEY,
    userId   INTEGER,
    title     VARCHAR(255),
    completed BOOLEAN DEFAULT FALSE
    );

INSERT INTO public.todos (userId, title, completed) VALUES
                                                        (1, 'delectus aut autem', false),
                                                        (1, 'quis ut nam facilis et officia qui', false),
                                                        (2, 'fugiat veniam minus', true),
                                                        (2, 'et porro tempora', false);