import { createContext, useState, useEffect } from "react";
import { jwtDecode } from "jwt-decode";

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [token, setToken] = useState(localStorage.getItem("jwt"));
  const [username, setUsername] = useState(null);

  const extractUsername = (jwt) => {
    try {
      const decoded = jwtDecode(jwt);
      return decoded.sub; // Spring stores username in "sub"
    } catch {
      return null;
    }
  };

  useEffect(() => {
    if (token) {
      setUsername(extractUsername(token));
    }
  }, [token]);

  const login = (jwt) => {
    localStorage.setItem("jwt", jwt);
    setToken(jwt);
  };

  const logout = () => {
    localStorage.removeItem("jwt");
    setToken(null);
    setUsername(null);
  };

  return (
    <AuthContext.Provider value={{ token, username, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};