MEDICAL JOURNAL

This is an application built for personal exercise purposes.
Eventually it may contain bad design and bad code smells.

Next steps
1. Integrate with Spring Security
2. Integrate with Spring Session
3. Add Redis for caching/session
4. Improve error/exeception handling

Main domain objects:
- user: oid, name, email, type, password, created date, topics
- journal: oid, title, description, author, topic, created date, created by
- topic: oid, title, description