import React from "react";
import { createRoot } from "react-dom/client";
import { useEffect, useState } from "react";
import "./styles.css";

// In development Vite proxies /api to Spring Boot, avoiding cross-origin browser requests.
const API = import.meta.env.VITE_API_URL || "";
const KEY = "learnify-session";
const getSession = () => JSON.parse(localStorage.getItem(KEY) || "null");
const uid = (u) => u?.userId ?? u?.UserId;
const qid = (q) => q?.questionId ?? q?.QuestionId;
const aid = (a) => a?.answerId ?? a?.AnswerId;

async function request(path, options = {}) {
  const session = getSession();
  const headers = { "Content-Type": "application/json", ...options.headers };
  if (session?.token) headers.Authorization = `Bearer ${session.token}`;
  const response = await fetch(`${API}${path}`, { ...options, headers });
  if (response.status === 204) return null;
  const raw = await response.text();
  let data = null;
  try {
    data = raw ? JSON.parse(raw) : null;
  } catch {
    data = null;
  }
  if (!response.ok)
    throw new Error(
      data?.message || raw || `Request failed (${response.status})`,
    );
  return data;
}

function App() {
  const [route, setRoute] = useState(location.hash.slice(1) || "/");
  const [user, setUser] = useState(getSession()?.user || null);
  useEffect(() => {
    const update = () => setRoute(location.hash.slice(1) || "/");
    addEventListener("hashchange", update);
    return () => removeEventListener("hashchange", update);
  }, []);
  const navigate = (nextRoute) => {
    const target = nextRoute.startsWith("/") ? nextRoute : `/${nextRoute}`;
    setRoute(target);
    if (location.hash !== `#${target}`) location.hash = target;
  };
  const login = async (auth) => {
    // Save the JWT before requesting the protected profile endpoint.
    localStorage.setItem(
      KEY,
      JSON.stringify({ token: auth.token, user: null }),
    );
    const profile = await request(`/api/user/id/${auth.userId}`);
    localStorage.setItem(
      KEY,
      JSON.stringify({ token: auth.token, user: profile }),
    );
    setUser(profile);
    location.hash = "/";
  };
  const logout = () => {
    localStorage.removeItem(KEY);
    setUser(null);
    location.hash = "/login";
  };
  if (!user)
    return route === "/register" ? (
      <Register done={login} />
    ) : route === "/login" ? (
      <Login done={login} />
    ) : (
      <Landing />
    );
  return (
    <PageErrorBoundary>
      <Layout user={user} logout={logout} route={route} navigate={navigate}>
        <Pages key={route} route={route} user={user} logout={logout} />
      </Layout>
    </PageErrorBoundary>
  );
}

function Landing() {
  return (
    <main className="landing">
      <nav>
        <Logo />
        <div>
          <a href="#/login">Log in</a>
          <a className="button" href="#/register">
            Create account
          </a>
        </div>
      </nav>
      <section className="hero">
        <p className="eyebrow">A LEARNING COMMUNITY</p>
        <h1>
          Ask better questions.
          <br />
          Find better answers.
        </h1>
        <p>
          Learnify helps students and teachers grow through useful discussion.
        </p>
        <a className="button" href="#/register">
          Get started
        </a>
      </section>
    </main>
  );
}
function Logo() {
  return (
    <a className="logo" href="#/">
      Learnify
    </a>
  );
}
function Auth({ title, children, footer }) {
  return (
    <main className="auth">
      <section>
        <Logo />
        <h1>{title}</h1>
        {children}
        <p className="foot">{footer}</p>
      </section>
      <aside>Learn something new every day.</aside>
    </main>
  );
}
function Login({ done }) {
  const [form, setForm] = useState({ username: "", password: "" }),
    [error, setError] = useState("");
  const submit = async (e) => {
    e.preventDefault();
    setError("");
    try {
      await done(
        await request("/api/auth/login", {
          method: "POST",
          body: JSON.stringify(form),
        }),
      );
    } catch (x) {
      setError(x.message);
    }
  };
  return (
    <Auth title="Welcome back.">
      <form onSubmit={submit}>
        <Input
          label="Username"
          value={form.username}
          change={(v) => setForm({ ...form, username: v })}
        />
        <Input
          label="Password"
          type="password"
          value={form.password}
          change={(v) => setForm({ ...form, password: v })}
        />
        <Error text={error} />
        <button className="button wide">Log in</button>
      </form>
      <span>
        New here? <a href="#/register">Create an account</a>
      </span>
    </Auth>
  );
}
function Register({ done }) {
  const [form, setForm] = useState({
      name: "",
      username: "",
      email: "",
      password: "",
      confirmPassword: "",
    }),
    [error, setError] = useState("");
  const submit = async (e) => {
    e.preventDefault();
    setError("");
    if (form.password !== form.confirmPassword) {
      setError("Passwords do not match.");
      return;
    }
    try {
      const { confirmPassword, ...registration } = form;
      await request("/api/auth/register", {
        method: "POST",
        body: JSON.stringify(registration),
      });
      await done(
        await request("/api/auth/login", {
          method: "POST",
          body: JSON.stringify({
            username: form.username,
            password: form.password,
          }),
        }),
      );
    } catch (x) {
      setError(x.message);
    }
  };
  return (
    <Auth title="Create your account.">
      <form onSubmit={submit}>
        <Input
          label="Name"
          value={form.name}
          change={(v) => setForm({ ...form, name: v })}
        />
        <Input
          label="Username"
          value={form.username}
          change={(v) => setForm({ ...form, username: v })}
        />
        <Input
          label="Email"
          type="email"
          value={form.email}
          change={(v) => setForm({ ...form, email: v })}
        />
        <Input
          label="Password"
          type="password"
          value={form.password}
          change={(v) => setForm({ ...form, password: v })}
        />
        <Input
          label="Confirm password"
          type="password"
          value={form.confirmPassword}
          change={(v) => setForm({ ...form, confirmPassword: v })}
        />
        <Error text={error} />
        <button className="button wide">Create account</button>
      </form>
      <span>
        Already registered? <a href="#/login">Log in</a>
      </span>
    </Auth>
  );
}

function Layout({ user, logout, route, navigate, children }) {
  const role = user.userRole;
  const links = [
    ["/", "Home"],
    ["/leaderboard", "Leaderboard"],
  ];
  if (role !== "ADMIN")
    links.splice(1, 0, [`/profile/${uid(user)}`, "Profile"]);
  if (role === "STUDENT") links.push(["/post-question", "Post question"]);
  if (role === "ADMIN") links.push(["/add-teacher", "Create teacher"]);
  return (
    <div className={`shell ${role.toLowerCase()}`}>
      <aside>
        <a
          className="logo"
          href="#/"
          onClick={(e) => {
            e.preventDefault();
            navigate("/");
          }}
        >
          Learnify
        </a>
        <div className="identity">
          <b>{user.name}</b>
          <small>{role.toLowerCase()}</small>
        </div>
        <nav>
          {links.map(([to, label]) => (
            <a
              key={to}
              className={route === to ? "selected" : ""}
              href={`#${to}`}
              onClick={(e) => {
                e.preventDefault();
                navigate(to);
              }}
            >
              {label}
            </a>
          ))}
        </nav>
      </aside>
      <div className="main">
        <header>
          <span>{role} dashboard</span>
          <button className="logout" onClick={logout}>
            Log out ↗
          </button>
        </header>
        <main>{children}</main>
      </div>
    </div>
  );
}
function Pages({ route, user, logout }) {
  if (route === "/") return <Home user={user} />;
  if (route.startsWith("/question/"))
    return <Home user={user} initialQuestionId={route.split("/").at(-1)} />;
  if (route === "/leaderboard") return <Leaderboard />;
  if (route === "/post-question" && user.userRole === "STUDENT")
    return <PostQuestion />;
  if (route === "/add-teacher" && user.userRole === "ADMIN")
    return <AddTeacher />;
  if (route.startsWith("/profile/"))
    return (
      <Profile id={route.split("/").at(-1)} viewer={user} logout={logout} />
    );
  return <Home user={user} />;
}
function Title({ title }) {
  return (
    <section className="title">
      <h1>{title}</h1>
    </section>
  );
}
function RoleInfo({ user }) {
  return (
    <span className="role-info">
      <a className="username-link" href={`#/profile/${uid(user)}`}>
        <b>@{user?.username}</b>
      </a>
      <em className={`badge ${user?.userRole?.toLowerCase()}`}>
        {user?.userRole}
      </em>
      {user?.userRole === "STUDENT" && (
        <small>{user?.reputationPoints || 0}</small>
      )}
    </span>
  );
}
function Home({ user, initialQuestionId }) {
  const [questions, setQuestions] = useState([]),
    [open, setOpen] = useState(null),
    [error, setError] = useState("");
  const load = () =>
    request("/api/question")
      .then((list) => {
        setQuestions(list);
        if (initialQuestionId)
          setOpen(
            list.find((q) => String(qid(q)) === String(initialQuestionId)) ||
              null,
          );
      })
      .catch((e) => setError(e.message));
  useEffect(() => {
    load();
  }, [initialQuestionId]);
  return (
    <>
      <Title title="Questions" />
      <Error text={error} />
      {questions.map((q) => (
        <article className="question" key={qid(q)}>
          <div>
            <p className="muted">
              <RoleInfo user={q.user} />
            </p>
            <h2>{q.content}</h2>
            <button className="plain" onClick={() => setOpen(q)}>
              {q.answerCount || 0} answers →
            </button>
          </div>
          {user.userRole === "ADMIN" ? (
            <Delete path={`/api/admin/question/${qid(q)}`} done={load} />
          ) : (
            uid(q.user) === uid(user) && (
              <Delete path={`/api/question/${qid(q)}`} done={load} />
            )
          )}
        </article>
      ))}
      {!questions.length && <p className="empty">No questions yet.</p>}
      {open && (
        <Answers
          question={open}
          user={user}
          close={() => {
            setOpen(null);
            load();
            location.hash = "/";
          }}
        />
      )}
    </>
  );
}
function Answers({ question, user, close }) {
  const [items, setItems] = useState([]),
    [content, setContent] = useState(""),
    [error, setError] = useState(""),
    [posting, setPosting] = useState(false),
    [message, setMessage] = useState(""),
    [voted, setVoted] = useState(new Set());
  const questionId = qid(question);
  const load = () =>
    request(`/api/answer/question/${questionId}`)
      .then(setItems)
      .catch((e) => setError(e.message));
  useEffect(() => {
    if (questionId) load();
    else setError("Question ID is missing.");
  }, [questionId]);
  const answer = async () => {
    if (!questionId) {
      setError("Question ID is missing. Refresh Home and try again.");
      return;
    }
    if (!content.trim()) {
      setError("Write an answer before posting.");
      return;
    }
    setPosting(true);
    setError("");
    try {
      await request(`/api/answer/question/${questionId}`, {
        method: "POST",
        body: JSON.stringify({ content: content.trim() }),
      });
      setContent("");
      setMessage("Answer posted.");
      await load();
    } catch (x) {
      setError(x.message || "Could not post answer.");
    } finally {
      setPosting(false);
    }
  };
  const vote = async (answerId) => {
    if (!answerId) return;
    setError("");
    try {
      await request(`/api/vote/answer/${answerId}`, { method: "PUT" });
      setVoted((old) => {
        const next = new Set(old);
        next.has(answerId) ? next.delete(answerId) : next.add(answerId);
        return next;
      });
      await load();
    } catch (x) {
      setError(x.message || "Could not update vote.");
    }
  };
  return (
    <div className="modal">
      <section>
        <button className="close" onClick={close}>
          ×
        </button>
        <h2>{question.content}</h2>
        <p className="muted">
          <RoleInfo user={question.user} />
        </p>
        <Error text={error} />
        {message && <p className="status">{message}</p>}
        {user.userRole !== "ADMIN" && (
          <div className="answer-form">
            <textarea
              value={content}
              placeholder="Write your answer…"
              onChange={(e) => setContent(e.target.value)}
            />
            <button
              type="button"
              className="button"
              disabled={posting}
              onClick={answer}
            >
              Post answer
            </button>
          </div>
        )}
        {items.map((a) => {
          const answerId = aid(a),
            isVoted = voted.has(answerId);
          return (
            <article className="answer" key={answerId}>
              <RoleInfo user={a.userResponseDto} />
              <p>{a.content}</p>
              <button
                className={`vote ${isVoted ? "voted" : ""}`}
                disabled={user.userRole === "ADMIN"}
                onClick={() => vote(answerId)}
              >
                {isVoted ? "Voted" : "Vote"} {a.voteCount || 0}
              </button>
              {user.userRole === "ADMIN" ? (
                <Delete path={`/api/admin/answer/${answerId}`} done={load} />
              ) : (
                uid(a.userResponseDto) === uid(user) && (
                  <Delete path={`/api/answer/${answerId}`} done={load} />
                )
              )}
            </article>
          );
        })}
      </section>
    </div>
  );
}
function Profile({ id, viewer, logout }) {
  const [user, setUser] = useState(null),
    [questions, setQuestions] = useState([]),
    [answers, setAnswers] = useState([]),
    [error, setError] = useState("");
  useEffect(() => {
    Promise.all([
      request(`/api/user/id/${id}`),
      request(`/api/question/user/${id}`),
      request(`/api/answer/user/${id}`),
    ])
      .then(([u, q, a]) => {
        setUser(u);
        setQuestions(q);
        setAnswers(a);
      })
      .catch((e) => setError(e.message));
  }, [id]);
  if (error)
    return (
      <>
        <Title title="Could not load this profile." />
        <Error text={error} />
      </>
    );
  if (!user) return <p>Loading profile…</p>;
  const isTeacher = user.userRole === "TEACHER",
    isOwnProfile = uid(user) === uid(viewer),
    canAdminDelete = viewer.userRole === "ADMIN" && !isOwnProfile;
  const removeUser = canAdminDelete ? (
    <Delete
      path={`/api/admin/user/${uid(user)}`}
      done={() => {
        location.hash = "/";
      }}
    />
  ) : (
    isOwnProfile && <Delete path="/api/user/me" done={logout} />
  );
  return (
    <>
      <section className="profile">
        <div>
          <p className="eyebrow">{user.userRole}</p>
          <h1>{user.name}</h1>
          <p>@{user.username}</p>
        </div>
        {!isTeacher && (
          <strong>
            {user.reputationPoints || 0}
            <small> reputation</small>
          </strong>
        )}
        {removeUser}
      </section>
      <div className={isTeacher ? "single-column" : "columns"}>
        {!isTeacher && (
          <List
            title="Questions"
            data={questions}
            keyName="questionId"
            render={(x) => <a href={`#/question/${qid(x)}`}>{x.content}</a>}
          />
        )}
        <List
          title="Answers"
          data={answers}
          keyName="answerId"
          render={(x) => (
            <a href={`#/question/${qid(x.questionResponseDto)}`}>{x.content}</a>
          )}
        />
      </div>
    </>
  );
}
function Leaderboard() {
  const [users, setUsers] = useState([]),
    [error, setError] = useState("");
  useEffect(() => {
    request("/api/user/top10")
      .then(setUsers)
      .catch((e) => setError(e.message));
  }, []);
  return (
    <>
      <Title title="Leaderboard" />
      <Error text={error} />
      <section className="board">
        {users.map((u, i) => (
          <a href={`#/profile/${uid(u)}`} key={uid(u)}>
            <b>{i + 1}</b>
            <span>
              {u.name}
              <small>@{u.username}</small>
            </span>
            <em>{u.reputationPoints || 0}</em>
          </a>
        ))}
      </section>
    </>
  );
}
function PostQuestion() {
  const [content, setContent] = useState(""),
    [error, setError] = useState("");
  const submit = async (e) => {
    e.preventDefault();
    try {
      await request("/api/question", {
        method: "POST",
        body: JSON.stringify({ content }),
      });
      location.hash = "/";
    } catch (x) {
      setError(x.message);
    }
  };
  return (
    <>
      <Title title="Post question" />
      <form className="card" onSubmit={submit}>
        <textarea
          value={content}
          onChange={(e) => setContent(e.target.value)}
          placeholder="What do you want to learn?"
          required
        />
        <Error text={error} />
        <button className="button">Post question</button>
      </form>
    </>
  );
}
function AddTeacher() {
  const [form, setForm] = useState({
      name: "",
      username: "",
      email: "",
      password: "",
      confirmPassword: "",
    }),
    [status, setStatus] = useState(""),
    [error, setError] = useState(""),
    [creating, setCreating] = useState(false);
  const create = async () => {
    if (
      !form.name ||
      !form.username ||
      !form.email ||
      !form.password ||
      !form.confirmPassword
    ) {
      setError("Fill in all fields.");
      return;
    }
    if (form.password !== form.confirmPassword) {
      setError("Passwords do not match.");
      return;
    }
    setCreating(true);
    setError("");
    setStatus("");
    try {
      const { confirmPassword, ...teacher } = form;
      await request("/api/admin/teacher", {
        method: "POST",
        body: JSON.stringify(teacher),
      });
      setStatus("Teacher created successfully.");
      setForm({
        name: "",
        username: "",
        email: "",
        password: "",
        confirmPassword: "",
      });
    } catch (x) {
      setStatus("");
      setError(x.message || "Could not create teacher.");
    } finally {
      setCreating(false);
    }
  };
  return (
    <>
      <Title
        label="ADMIN"
        title="Create a teacher."
        description="Teachers can answer and vote, but cannot post questions."
      />
      <div className="card grid">
        <Input
          label="Name"
          value={form.name}
          change={(v) => setForm({ ...form, name: v })}
        />
        <Input
          label="Username"
          value={form.username}
          change={(v) => setForm({ ...form, username: v })}
        />
        <Input
          label="Email"
          type="email"
          value={form.email}
          change={(v) => setForm({ ...form, email: v })}
        />
        <Input
          label="Password"
          type="password"
          value={form.password}
          change={(v) => setForm({ ...form, password: v })}
        />
        <Input
          label="Confirm password"
          type="password"
          value={form.confirmPassword}
          change={(v) => setForm({ ...form, confirmPassword: v })}
        />
        <Error text={error} />
        {status && <p className="status">{status}</p>}
        <button
          type="button"
          className="button"
          disabled={creating}
          onClick={create}
        >
          {creating ? "Create teacher" : "Create teacher"}
        </button>
      </div>
    </>
  );
}
function Delete({ path, done }) {
  const click = async (event) => {
    event.preventDefault();
    event.stopPropagation();
    if (!window.confirm("Delete this item?")) return;
    try {
      await request(path, { method: "DELETE" });
      done();
    } catch (error) {
      window.alert(error.message || "Could not delete this item.");
    }
  };
  return (
    <button type="button" className="delete" onClick={click}>
      Delete
    </button>
  );
}
function Input({ label, type = "text", value, change }) {
  return (
    <label>
      {label}
      <input
        type={type}
        value={value}
        onChange={(e) => change(e.target.value)}
        required
      />
    </label>
  );
}
function Error({ text }) {
  return text && <p className="error">{text}</p>;
}
function List({ title, data, keyName, render }) {
  return (
    <section>
      <h2>{title}</h2>
      {data.map((x) => (
        <article className="item" key={x[keyName]}>
          {render(x)}
        </article>
      ))}
      {!data.length && <p className="empty">Nothing here yet.</p>}
    </section>
  );
}
class PageErrorBoundary extends React.Component {
  state = { error: null };
  static getDerivedStateFromError(error) {
    return { error };
  }
  componentDidUpdate(previous) {
    if (previous.children !== this.props.children && this.state.error)
      this.setState({ error: null });
  }
  render() {
    return this.state.error ? (
      <main className="app-error">
        <h1>Page could not load.</h1>
        <p>{this.state.error.message}</p>
        <a className="button" href="#/">
          Back home
        </a>
      </main>
    ) : (
      this.props.children
    );
  }
}
createRoot(document.getElementById("root")).render(<App />);
