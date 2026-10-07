// src/api/client.js
import axios from "axios";

const client = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL, // set in .env
});

export const getHealth = () => client.get("/api/health");
export const getDrivers = () => client.get("/api/drivers");
export const getDriverById = (id) => client.get(`/api/drivers/${id}`);
export const getConstructors = () => client.get('/api/constructors');
export const getConstructorById = (id) => client.get(`/api/constructors/${id}`);
export const getStandings = (season) => client.get('/api/drivers/standings');

export default client;
