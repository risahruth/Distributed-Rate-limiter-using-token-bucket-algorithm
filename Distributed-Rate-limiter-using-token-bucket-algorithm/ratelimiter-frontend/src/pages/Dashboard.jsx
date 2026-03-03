import { useEffect, useState, useContext } from "react";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "../auth/AuthContext";
import api from "../api/axios";
import {
  PieChart, Pie, Cell, Tooltip,
  LineChart, Line, XAxis, YAxis, CartesianGrid, Legend,
  ResponsiveContainer
} from "recharts";

const Dashboard = () => {
  const { token, username, logout } = useContext(AuthContext);
  const navigate = useNavigate();
  const [logs, setLogs] = useState([]);

  const COLORS = ["#0088FE", "#FF4C4C"];

  const pieData = [
    { name: "ALLOWED", value: logs.filter(l => l.status === "ALLOWED").length },
    { name: "BLOCKED", value: logs.filter(l => l.status === "BLOCKED").length },
  ];

  const lineData = logs
    .map(log => ({
      time: new Date(log.timestamp).toLocaleTimeString(),
      allowed: log.status === "ALLOWED" ? 1 : 0,
      blocked: log.status === "BLOCKED" ? 1 : 0,
    }))
    .reduce((acc, curr) => {
      const existing = acc.find(item => item.time === curr.time);
      if (existing) {
        existing.allowed += curr.allowed;
        existing.blocked += curr.blocked;
      } else {
        acc.push(curr);
      }
      return acc;
    }, []);

  const handleFetchLogs = async () => {
    try {
      console.log("Fetching logs...");
      const res = await api.get(`/logs/${username}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      console.log("Logs received:", res.data);
      console.log("TOKEN:", token);
      console.log("USERNAME:", username);
      setLogs(res.data);
    } catch (err) {
      console.error("Fetch error:", err.response || err.message);
    }
  };

  useEffect(() => {
    if (token && username) {
      handleFetchLogs();
    }
  }, [token, username]);

  const handleLogout = () => {
    if (window.confirm("Are you sure you want to logout?")) {
      logout();
      navigate("/login");
    }
  };

  return (
    <div style={{
      width: "100%",
      minHeight: "100vh",
      background: "#1e1e2f",
      color: "white",
      padding: "40px",
      boxSizing: "border-box"
    }}>
      <div style={{ width: "100%" }}>

        {/* Header */}
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", flexWrap: "wrap" }}>
          <h2>JWT Rate Limiter Dashboard</h2>
          <div style={{ display: "flex", gap: "10px", marginTop: "10px" }}>
            <button
              onClick={handleFetchLogs}
              style={{
                padding: "10px 20px",
                backgroundColor: "#0088FE",
                border: "none",
                borderRadius: "5px",
                cursor: "pointer",
                color: "white",
              }}
            >
              Fetch Logs
            </button>

            <button
              onClick={handleLogout}
              style={{
                padding: "10px 20px",
                backgroundColor: "#FF4C4C",
                border: "none",
                borderRadius: "5px",
                cursor: "pointer",
                color: "white",
              }}
            >
              Logout
            </button>
          </div>
        </div>

        {/* Charts */}
        <div style={{ display: "flex", gap: "30px", marginTop: "40px", flexWrap: "wrap" }}>

          {/* Pie */}
          <div style={{
            backgroundColor: "#2e2e44",
            borderRadius: "10px",
            padding: "20px",
            flex: "1 1 400px",
            textAlign: "center"
          }}>
            <h3>Requests Distribution</h3>
            <ResponsiveContainer width="100%" height={300}>
              <PieChart>
                <Pie
                  data={pieData}
                  dataKey="value"
                  nameKey="name"
                  cx="50%"
                  cy="50%"
                  outerRadius={100}
                  label
                >
                  {pieData.map((entry, index) => (
                    <Cell key={index} fill={COLORS[index % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          </div>

          {/* Line */}
          <div style={{
            backgroundColor: "#2e2e44",
            borderRadius: "10px",
            padding: "20px",
            flex: "2 1 600px"
          }}>
            <h3>Requests Over Time</h3>
            <ResponsiveContainer width="100%" height={300}>
              <LineChart data={lineData}>
                <CartesianGrid strokeDasharray="3 3" stroke="#555" />
                <XAxis dataKey="time" stroke="#fff" />
                <YAxis stroke="#fff" />
                <Tooltip />
                <Legend />
                <Line type="monotone" dataKey="allowed" stroke="#00FF00" />
                <Line type="monotone" dataKey="blocked" stroke="#FF4C4C" />
              </LineChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Table */}
        <div style={{
          backgroundColor: "#2e2e44",
          borderRadius: "10px",
          padding: "20px",
          marginTop: "40px",
          overflowX: "auto"
        }}>
          <h3>Recent Requests</h3>
          <table style={{ width: "100%", borderCollapse: "collapse" }}>
            <thead>
              <tr>
                <th>Endpoint</th>
                <th>Status</th>
                <th>Tokens Remaining</th>
                <th>Timestamp</th>
              </tr>
            </thead>
            <tbody>
              {logs.slice().reverse().map((log, idx) => (
                <tr key={idx} style={{ textAlign: "center", borderBottom: "1px solid #444" }}>
                  <td>{log.endpoint}</td>
                  <td style={{ color: log.status === "ALLOWED" ? "#00FF00" : "#FF4C4C" }}>
                    {log.status}
                  </td>
                  <td>{log.tokensRemaining}</td>
                  <td>{new Date(log.timestamp).toLocaleString()}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

      </div>
    </div>
  );
};

export default Dashboard;