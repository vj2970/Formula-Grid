// src/api/client.js
import axios from "axios";

const client = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL, // set in .env
});

export const getDrivers = () => client.get("/api/drivers");
export const getDriverById = (id) => client.get(`/api/drivers/${id}`);
export const getConstructors = () => client.get("/api/constructors");
export const getStandings = (season) => client.get(`/api/standings/${season}`);

export default client;
