<h1>MEDICAL JOURNAL</h1>

This is an application built for personal exercise purposes.<br>
Eventually it may contain bad design and bad code smells.<br>

Next steps<br>
1. Integrate with Spring Security<br>
2. Integrate with Spring Session<br>
3. Add Redis for caching/session<br>
4. Improve error/exeception handling<br>
<br>
Main domain objects:<br>
- user: oid, name, email, type, password, created date, topics<br>
- journal: oid, title, description, author, topic, created date, created by<br>
- topic: oid, title, description<br>