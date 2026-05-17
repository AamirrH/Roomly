import React from "react";
import { createRoot } from "react-dom/client";
import {
  ArrowRight,
  BadgeIndianRupee,
  BedDouble,
  Bell,
  Building2,
  CalendarDays,
  Check,
  ChevronLeft,
  ChevronRight,
  CircleDollarSign,
  ConciergeBell,
  Download,
  Dumbbell,
  Edit3,
  Heart,
  Hotel,
  LayoutDashboard,
  LockKeyhole,
  MapPin,
  Menu,
  Plus,
  Search,
  ShieldCheck,
  SlidersHorizontal,
  Sparkles,
  Star,
  Trash2,
  UserRound,
  Users,
  Utensils,
  Waves,
  Wifi,
  X
} from "lucide-react";
import "./styles.css";

const API_BASE = import.meta.env.VITE_API_BASE_URL || "http://localhost:8081";
const ACCESS_TOKEN_KEY = "roomlyAccessToken";
const USER_KEY = "roomlyUser";
const API_CHECK_PATH = "/roomly/api/v1/hotels/search?city=Mumbai&checkInDate=2026-05-20&checkOutDate=2026-05-23&numberOfRooms=1&pageNumber=0&pageSize=1";

const img = {
  heroLeft: "https://images.unsplash.com/photo-1618773928121-c32242e63f39?auto=format&fit=crop&w=1100&q=88",
  heroRight: "https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=1100&q=88",
  paris: "https://images.unsplash.com/photo-1590490360182-c33d57733427?auto=format&fit=crop&w=1000&q=88",
  bali: "https://images.unsplash.com/photo-1540541338287-41700207dee6?auto=format&fit=crop&w=1000&q=88",
  manhattan: "https://images.unsplash.com/photo-1582719508461-905c673771fd?auto=format&fit=crop&w=1000&q=88",
  searchA: "https://images.unsplash.com/photo-1551882547-ff40c63fe5fa?auto=format&fit=crop&w=1000&q=88",
  searchB: "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=1000&q=88",
  searchC: "https://images.unsplash.com/photo-1564501049412-61c2a3083791?auto=format&fit=crop&w=1000&q=88",
  detailHero: "https://images.unsplash.com/photo-1600566753190-17f0baa2a6c3?auto=format&fit=crop&w=1400&q=88",
  bath: "https://images.unsplash.com/photo-1584622650111-993a426fbf0a?auto=format&fit=crop&w=900&q=88",
  breakfast: "https://images.unsplash.com/photo-1551218808-94e220e084d2?auto=format&fit=crop&w=900&q=88",
  pool: "https://images.unsplash.com/photo-1571896349842-33c89424de2d?auto=format&fit=crop&w=900&q=88",
  roomA: "https://images.unsplash.com/photo-1591088398332-8a7791972843?auto=format&fit=crop&w=1000&q=88",
  roomB: "https://images.unsplash.com/photo-1609949279531-cf48d64bed89?auto=format&fit=crop&w=1000&q=88",
  roomC: "https://images.unsplash.com/photo-1618221195710-dd6b41faaea6?auto=format&fit=crop&w=1000&q=88",
  avatarA: "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=200&q=80",
  avatarB: "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=200&q=80",
  avatarC: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=200&q=80"
};

const fallbackHotels = [
  {
    id: 1,
    name: "The Ritz Roomly",
    city: "Mumbai",
    address: "Marine Drive, Mumbai",
    rating: 4.9,
    photos: [img.paris],
    amenities: ["Spa", "Pool", "Breakfast"],
    estimatedStartingPrice: 12800
  },
  {
    id: 2,
    name: "Ubud Sanctuary",
    city: "Goa",
    address: "Mandrem Beach, Goa",
    rating: 4.8,
    photos: [img.bali],
    amenities: ["Wellness", "Beach", "Dining"],
    estimatedStartingPrice: 9300
  },
  {
    id: 3,
    name: "The Loft Jaipur",
    city: "Jaipur",
    address: "Civil Lines, Jaipur",
    rating: 5,
    photos: [img.manhattan],
    amenities: ["Heritage", "Rooftop", "Valet"],
    estimatedStartingPrice: 11600
  },
  {
    id: 4,
    name: "Maison Emerald",
    city: "Delhi",
    address: "Chanakyapuri, Delhi",
    rating: 4.7,
    photos: [img.searchA],
    amenities: ["Gym", "Fine Dining", "Spa"],
    estimatedStartingPrice: 14800
  },
  {
    id: 5,
    name: "The Peninsula Roomly",
    city: "Bengaluru",
    address: "MG Road, Bengaluru",
    rating: 4.8,
    photos: [img.searchB],
    amenities: ["Business Lounge", "Pool"],
    estimatedStartingPrice: 10200
  },
  {
    id: 6,
    name: "Le Meurice Roomly",
    city: "Udaipur",
    address: "Lake Pichola, Udaipur",
    rating: 4.9,
    photos: [img.searchC],
    amenities: ["Lake View", "Butler", "Breakfast"],
    estimatedStartingPrice: 17200
  }
];

const fallbackRooms = [
  {
    id: 11,
    hotelId: 1,
    type: "Deluxe King Room",
    capacity: 2,
    basePrice: 8200,
    estimatedTotalPrice: 24600,
    estimatedAverageNightlyPrice: 8200,
    amenities: ["Free WiFi", "Nespresso", "Marble Bath", "Garden View"],
    photos: [img.roomA]
  },
  {
    id: 12,
    hotelId: 1,
    type: "Executive Suite",
    capacity: 3,
    basePrice: 18400,
    estimatedTotalPrice: 55200,
    estimatedAverageNightlyPrice: 18400,
    amenities: ["Butler Service", "Private Lounge", "Mini Bar", "City View"],
    photos: [img.roomB]
  },
  {
    id: 13,
    hotelId: 1,
    type: "Superior Junior Suite",
    capacity: 2,
    basePrice: 12800,
    estimatedTotalPrice: 38400,
    estimatedAverageNightlyPrice: 12800,
    amenities: ["Balcony", "Rainfall Shower", "Work Desk", "Breakfast"],
    photos: [img.roomC]
  }
];

const sampleBookings = [
  {
    id: "RM-92831",
    hotelName: "The Kanso Sanctuary",
    roomType: "Zen Garden Suite",
    city: "Kyoto",
    checkInDate: "2026-06-12",
    checkoutDate: "2026-06-18",
    status: "CONFIRMED",
    finalCalculatedPrice: 184000,
    photo: img.heroLeft
  },
  {
    id: "RM-77402",
    hotelName: "Astra Heritage Villa",
    roomType: "Cliffside Infinity Studio",
    city: "Goa",
    checkInDate: "2026-07-04",
    checkoutDate: "2026-07-07",
    status: "RESERVED",
    finalCalculatedPrice: 210000,
    photo: img.bali
  },
  {
    id: "RM-44112",
    hotelName: "The Emerald Estate",
    roomType: "Standard Room",
    city: "Delhi",
    checkInDate: "2026-04-01",
    checkoutDate: "2026-04-03",
    status: "CANCELLED",
    finalCalculatedPrice: 45000,
    photo: img.searchA
  }
];

function money(value) {
  if (value === null || value === undefined || Number.isNaN(Number(value))) return "Price on request";
  return new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 0
  }).format(value);
}

function nights(query) {
  const start = new Date(query.checkInDate);
  const end = new Date(query.checkOutDate);
  const diff = Math.ceil((end - start) / 86400000);
  return Number.isFinite(diff) && diff > 0 ? diff : 1;
}

async function request(path, options = {}) {
  const token = window.localStorage.getItem(ACCESS_TOKEN_KEY);
  const response = await fetch(`${API_BASE}${path}`, {
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...(options.headers || {})
    },
    ...options
  });

  if (!response.ok) {
    let message = `Request failed with ${response.status}`;
    try {
      const payload = await response.clone().json();
      message = payload?.message || payload?.error || message;
    } catch {
      const text = await response.text();
      message = text || message;
    }
    const error = new Error(message);
    error.status = response.status;
    throw error;
  }
  const text = await response.text();
  return text ? JSON.parse(text) : null;
}

function loadRazorpayCheckout() {
  if (window.Razorpay) return Promise.resolve(true);

  return new Promise((resolve) => {
    const script = document.createElement("script");
    script.src = "https://checkout.razorpay.com/v1/checkout.js";
    script.onload = () => resolve(true);
    script.onerror = () => resolve(false);
    document.body.appendChild(script);
  });
}

function firstPhoto(item, fallback) {
  return item?.photos?.[0] || fallback;
}

function displayHotelName(hotel) {
  return hotel?.hotelName || hotel?.name || `Hotel #${hotel?.id || "new"}`;
}

function csvToList(value) {
  return String(value || "")
    .split(",")
    .map((item) => item.trim())
    .filter(Boolean);
}

function listToCsv(value) {
  return Array.isArray(value) ? value.join(", ") : "";
}

function responseItems(payload) {
  if (Array.isArray(payload)) return payload;
  if (Array.isArray(payload?.content)) return payload.content;
  if (Array.isArray(payload?.data)) return payload.data;
  if (Array.isArray(payload?.items)) return payload.items;
  if (Array.isArray(payload?._embedded?.hotels)) return payload._embedded.hotels;
  if (Array.isArray(payload?._embedded?.rooms)) return payload._embedded.rooms;
  return [];
}

function parseJwtPayload(token) {
  if (!token) return null;
  try {
    const [, payload] = token.split(".");
    const normalized = payload.replace(/-/g, "+").replace(/_/g, "/");
    const padded = normalized.padEnd(Math.ceil(normalized.length / 4) * 4, "=");
    return JSON.parse(window.atob(padded));
  } catch {
    return null;
  }
}

function normalizeRoles(value) {
  if (!value) return [];
  if (Array.isArray(value)) return value.map((role) => String(role).trim().toUpperCase()).filter(Boolean);
  return String(value)
    .replace(/[[\]\s]/g, "")
    .split(",")
    .map((role) => role.trim().toUpperCase())
    .filter(Boolean);
}

function sessionFromToken(token, fallback = {}) {
  const payload = parseJwtPayload(token);
  const roles = normalizeRoles(payload?.roles || fallback.roles || fallback.role);
  return {
    username: payload?.username || fallback.username || fallback.email?.split("@")[0] || "Roomly",
    email: payload?.email || fallback.email || "",
    roles: roles.length ? roles : ["USER"],
    role: roles[0] || "USER"
  };
}

function userRoles(user) {
  return normalizeRoles(user?.roles || user?.role);
}

function hasAnyRole(user, allowedRoles) {
  const roles = userRoles(user);
  return roles.some((role) => allowedRoles.includes(role));
}

function isGuestUser(user) {
  return hasAnyRole(user, ["USER"]) && !isManagerUser(user);
}

function isManagerUser(user) {
  return hasAnyRole(user, ["HOTEL_MANAGER", "HOTEL_ADMIN", "ROOMLY_ADMIN"]);
}

function roleLabel(user) {
  const role = userRoles(user)[0] || "Guest";
  return role.replace(/_/g, " ").toLowerCase().replace(/\b\w/g, (letter) => letter.toUpperCase());
}

function liveItemCount(payload) {
  return responseItems(payload).length;
}

function App() {
  const [view, setView] = React.useState("home");
  const [menuOpen, setMenuOpen] = React.useState(false);
  const [authMode, setAuthMode] = React.useState("login");
  const [authUser, setAuthUser] = React.useState(() => {
    try {
      const storedUser = JSON.parse(window.localStorage.getItem(USER_KEY));
      const token = window.localStorage.getItem(ACCESS_TOKEN_KEY);
      return token ? sessionFromToken(token, storedUser || {}) : storedUser;
    } catch {
      return null;
    }
  });
  const [query, setQuery] = React.useState({
    city: "Mumbai",
    checkInDate: "2026-05-20",
    checkOutDate: "2026-05-23",
    numberOfRooms: 1
  });
  const [hotels, setHotels] = React.useState(fallbackHotels);
  const [rooms, setRooms] = React.useState(fallbackRooms);
  const [bookings, setBookings] = React.useState(sampleBookings);
  const [selectedHotel, setSelectedHotel] = React.useState(fallbackHotels[0]);
  const [selectedRoom, setSelectedRoom] = React.useState(fallbackRooms[0]);
  const [checkoutOpen, setCheckoutOpen] = React.useState(false);
  const [toast, setToast] = React.useState("");
  const [loading, setLoading] = React.useState(false);
  const [apiStatus, setApiStatus] = React.useState({
    state: "checking",
    label: "Checking API",
    detail: API_BASE
  });

  React.useEffect(() => {
    const controller = new AbortController();
    const timeout = window.setTimeout(() => controller.abort(), 3500);

    async function checkBackend() {
      try {
        const response = await fetch(`${API_BASE}${API_CHECK_PATH}`, {
          headers: { "Content-Type": "application/json" },
          signal: controller.signal
        });

        if (!response.ok) {
          setApiStatus({
            state: "error",
            label: "Backend error",
            detail: `HTTP ${response.status}`
          });
          return;
        }

        const text = await response.text();
        const payload = text ? JSON.parse(text) : null;
        const count = liveItemCount(payload);
        setApiStatus({
          state: count > 0 ? "data" : "empty",
          label: count > 0 ? "Live data" : "Backend live",
          detail: count > 0 ? `${count} result${count === 1 ? "" : "s"}` : "No data returned"
        });
      } catch (error) {
        if (error.name === "AbortError") return;
        setApiStatus({
          state: "offline",
          label: "Not connected",
          detail: API_BASE
        });
      } finally {
        window.clearTimeout(timeout);
      }
    }

    checkBackend();
    return () => {
      window.clearTimeout(timeout);
      controller.abort();
    };
  }, []);

  function noteApiSuccess(payload) {
    const count = liveItemCount(payload);
    setApiStatus({
      state: count > 0 ? "data" : "empty",
      label: count > 0 ? "Live data" : "Backend live",
      detail: count > 0 ? `${count} result${count === 1 ? "" : "s"}` : "No data returned"
    });
  }

  function noteApiFailure(error) {
    setApiStatus({
      state: error?.status ? "error" : "offline",
      label: error?.status ? "Backend error" : "Not connected",
      detail: error?.status ? `HTTP ${error.status}` : API_BASE
    });
  }

  function notify(message) {
    setToast(message);
    window.setTimeout(() => setToast(""), 2800);
  }

  function openAuth(mode = "login") {
    setAuthMode(mode);
    setView("auth");
    setMenuOpen(false);
  }

  async function submitAuth(mode, form) {
    setLoading(true);
    try {
      if (mode === "signup") {
        await request("/roomly/api/v1/signup", {
          method: "POST",
          body: JSON.stringify({
            username: form.username,
            email: form.email,
            password: form.password
          })
        });
      }

      const session = await request("/roomly/api/v1/login", {
        method: "POST",
        body: JSON.stringify({
          email: form.email,
          password: form.password
        })
      });

      if (session?.accessToken) {
        window.localStorage.setItem(ACCESS_TOKEN_KEY, session.accessToken);
      }

      const nextUser = sessionFromToken(session?.accessToken, {
        username: form.username,
        email: form.email
      });
      window.localStorage.setItem(USER_KEY, JSON.stringify(nextUser));
      setAuthUser(nextUser);
      notify(mode === "signup" ? "Welcome to Roomly. Your guest account is ready." : "Welcome back. Your session is ready.");
      setView(isManagerUser(nextUser) ? "manager" : "home");
    } catch (error) {
      notify(error?.message || (mode === "signup" ? "Signup failed. Check your details and try again." : "Login failed. Check your credentials."));
    } finally {
      setLoading(false);
    }
  }

  function logout() {
    window.localStorage.removeItem(ACCESS_TOKEN_KEY);
    window.localStorage.removeItem(USER_KEY);
    setAuthUser(null);
    notify("Signed out of Roomly.");
    setView("home");
  }

  function updateQuery(event) {
    const { name, value } = event.target;
    setQuery((current) => ({
      ...current,
      [name]: name === "numberOfRooms" ? Number(value) : value
    }));
  }

  async function searchHotels(event) {
    event?.preventDefault();
    setLoading(true);
    try {
      const params = new URLSearchParams({
        city: query.city,
        checkInDate: query.checkInDate,
        checkOutDate: query.checkOutDate,
        numberOfRooms: query.numberOfRooms,
        pageNumber: 0,
        pageSize: 12
      });
      const page = await request(`/roomly/api/v1/hotels/search?${params.toString()}`);
      const content = responseItems(page);
      noteApiSuccess(content);
      setHotels(content.length ? content : fallbackHotels);
      if (!content.length) notify("No live stays returned yet. Showing editorial sample stays.");
    } catch (error) {
      noteApiFailure(error);
      setHotels(fallbackHotels);
      notify("Backend request failed. Showing editorial sample stays.");
    } finally {
      setView("hotels");
      setLoading(false);
    }
  }

  async function openHotel(hotel) {
    setSelectedHotel(hotel);
    setLoading(true);
    try {
      const params = new URLSearchParams({
        checkInDate: query.checkInDate,
        checkOutDate: query.checkOutDate,
        numberOfRooms: query.numberOfRooms
      });
      const availableRooms = await request(`/roomly/api/v1/hotels/${hotel.id}?${params.toString()}`);
      const roomContent = responseItems(availableRooms);
      noteApiSuccess(roomContent);
      setRooms(roomContent.length ? roomContent : fallbackRooms.map((room) => ({ ...room, hotelId: hotel.id })));
      if (!roomContent.length) notify("No live rooms returned yet. Showing sample room selections.");
    } catch (error) {
      noteApiFailure(error);
      setRooms(fallbackRooms.map((room) => ({ ...room, hotelId: hotel.id })));
      notify("Room request failed. Showing sample room selections.");
    } finally {
      setView("detail");
      setLoading(false);
    }
  }

  function selectRoom(room) {
    if (!authUser) {
      notify("Sign in with a guest account to reserve a room.");
      openAuth("login");
      return;
    }
    if (!isGuestUser(authUser)) {
      notify("Manager accounts cannot create guest bookings. Use a guest account to book stays.");
      return;
    }
    setSelectedRoom(room);
    setCheckoutOpen(true);
  }

  async function confirmBooking(form) {
    if (!isGuestUser(authUser)) {
      notify("Use a guest account to complete reservations.");
      setCheckoutOpen(false);
      return;
    }
    setLoading(true);
    try {
      const checkoutReady = await loadRazorpayCheckout();
      if (!checkoutReady) {
        throw new Error("Razorpay checkout could not be loaded");
      }

      const created = await request("/roomly/api/v1/bookings", {
        method: "POST",
        body: JSON.stringify({
          roomId: selectedRoom.id,
          hotelId: selectedHotel.id,
          city: selectedHotel.city || query.city,
          checkInDate: form.checkInDate,
          checkOutDate: form.checkOutDate,
          numberOfRooms: form.rooms
        })
      });
      const paymentOrder = await request("/roomly/api/v1/payments/orders", {
        method: "POST",
        body: JSON.stringify({
          bookingId: created.id
        })
      });

      const razorpayResult = await openRazorpayCheckout(paymentOrder, created);
      const verifiedPayment = await request("/roomly/api/v1/payments/verify", {
        method: "POST",
        body: JSON.stringify({
          bookingId: created.id,
          razorpayOrderId: razorpayResult.razorpay_order_id,
          razorpayPaymentId: razorpayResult.razorpay_payment_id,
          razorpaySignature: razorpayResult.razorpay_signature
        })
      });

      setBookings((current) => [
        {
          ...created,
          status: verifiedPayment?.bookingStatus || "CONFIRMED",
          id: created?.id || `RM-${Date.now()}`,
          hotelName: selectedHotel.name || `Roomly ${selectedHotel.city}`,
          roomType: selectedRoom.type,
          city: selectedHotel.city,
          photo: firstPhoto(selectedRoom, img.roomA)
        },
        ...current
      ]);
      notify("Payment verified. Your booking is confirmed.");
      setCheckoutOpen(false);
      setView("bookings");
    } catch (error) {
      notify(error?.message || "Payment could not be completed. Please try again.");
    } finally {
      setLoading(false);
    }
  }

  function openRazorpayCheckout(paymentOrder, booking) {
    return new Promise((resolve, reject) => {
      if (!window.Razorpay) {
        reject(new Error("Razorpay checkout is not available"));
        return;
      }

      const checkout = new window.Razorpay({
        key: paymentOrder.razorpayKeyId,
        amount: paymentOrder.amountInPaise,
        currency: paymentOrder.currency,
        name: "Roomly",
        description: `Booking #${booking.id}`,
        order_id: paymentOrder.razorpayOrderId,
        prefill: {
          name: authUser?.username || "",
          email: authUser?.email || ""
        },
        notes: {
          bookingId: String(booking.id),
          hotelId: String(selectedHotel.id),
          roomId: String(selectedRoom.id)
        },
        theme: {
          color: "#173b2f"
        },
        handler: resolve,
        modal: {
          ondismiss: () => reject(new Error("Payment was cancelled before completion."))
        }
      });

      checkout.on("payment.failed", (response) => {
        reject(new Error(response?.error?.description || "Payment failed. Please try again."));
      });

      checkout.open();
    });
  }

  function navigate(next) {
    if (next === "manager" && !isManagerUser(authUser)) {
      notify(authUser ? "Your account does not have manager access." : "Sign in with a manager account to open the console.");
      if (!authUser) openAuth("login");
      return;
    }
    if (next === "bookings" && !isGuestUser(authUser)) {
      notify(authUser ? "Manager accounts do not have guest bookings." : "Sign in with a guest account to view bookings.");
      if (!authUser) openAuth("login");
      return;
    }
    setView(next);
    setMenuOpen(false);
  }

  return (
    <div className="app">
      <Navbar
        view={view}
        navigate={navigate}
        menuOpen={menuOpen}
        setMenuOpen={setMenuOpen}
        searchHotels={searchHotels}
        authUser={authUser}
        openAuth={openAuth}
        logout={logout}
        apiStatus={apiStatus}
      />
      {loading && <div className="loading-strip">Curating your request</div>}
      {view === "home" && <Landing query={query} updateQuery={updateQuery} searchHotels={searchHotels} openHotel={openHotel} />}
      {view === "hotels" && <SearchResults hotels={hotels} query={query} updateQuery={updateQuery} searchHotels={searchHotels} openHotel={openHotel} />}
      {view === "detail" && <HotelDetail hotel={selectedHotel} rooms={rooms} query={query} navigate={navigate} selectRoom={selectRoom} authUser={authUser} />}
      {view === "bookings" && (isGuestUser(authUser) ? <MyBookings bookings={bookings} navigate={navigate} /> : <AccessPanel title="Guest account required" text="Bookings are attached to guest accounts. Sign in as a guest to view reservations." action="Sign In" onAction={() => openAuth("login")} />)}
      {view === "manager" && (isManagerUser(authUser) ? <AdminDashboard authUser={authUser} /> : <AccessPanel title="Manager access required" text="The hotel operations console is visible only to hotel managers and Roomly admins." action="Sign In" onAction={() => openAuth("login")} />)}
      {view === "auth" && <AuthPage mode={authMode} setMode={setAuthMode} onSubmit={submitAuth} />}
      {checkoutOpen && (
        <CheckoutModal
          room={selectedRoom}
          hotel={selectedHotel}
          query={query}
          onClose={() => setCheckoutOpen(false)}
          onConfirm={confirmBooking}
        />
      )}
      {toast && <Toast message={toast} />}
      {view !== "manager" && <Footer />}
    </div>
  );
}

function Navbar({ view, navigate, menuOpen, setMenuOpen, searchHotels, authUser, openAuth, logout, apiStatus }) {
  const links = [
    ["hotels", "Hotels"],
    ["detail", "Rooms"],
    ...(isGuestUser(authUser) ? [["bookings", "My Bookings"]] : []),
    ...(isManagerUser(authUser) ? [["manager", "Manager Console"]] : [])
  ];

  return (
    <header className="navbar">
      <button className="logo" onClick={() => navigate("home")} type="button">
        Room<span>ly</span>
      </button>
      <nav className={menuOpen ? "nav-links open" : "nav-links"}>
        {links.map(([key, label]) => (
          <button className={view === key ? "active" : ""} key={key} onClick={() => navigate(key)} type="button">
            {label}
          </button>
        ))}
      </nav>
      <div className="nav-actions">
        <ApiStatusPill status={apiStatus} />
        {authUser ? (
          <div className="session-chip">
            <span>{authUser.username?.slice(0, 1) || "R"}</span>
            <small>{roleLabel(authUser)}</small>
            <button onClick={logout} type="button">Sign Out</button>
          </div>
        ) : (
          <button className="soft-button auth-nav" onClick={() => openAuth("login")} type="button">
            Sign In
          </button>
        )}
        <button className="ink-button" onClick={isManagerUser(authUser) ? () => navigate("manager") : searchHotels} type="button">
          {isManagerUser(authUser) ? "Manage" : "Book Now"}
        </button>
        <button className="menu-button" onClick={() => setMenuOpen(!menuOpen)} type="button">
          {menuOpen ? <X size={20} /> : <Menu size={20} />}
        </button>
      </div>
    </header>
  );
}

function ApiStatusPill({ status }) {
  return (
    <div className={`api-status ${status.state}`} title={`${status.label}: ${status.detail}`}>
      <span />
      <div>
        <strong>{status.label}</strong>
        <small>{status.detail}</small>
      </div>
    </div>
  );
}

function AccessPanel({ title, text, action, onAction }) {
  return (
    <main className="access-page">
      <section className="access-panel">
        <div><ShieldCheck size={28} /></div>
        <p className="eyebrow">Role Protected</p>
        <h1>{title}</h1>
        <p>{text}</p>
        <button className="forest-button" onClick={onAction} type="button">{action}</button>
      </section>
    </main>
  );
}

function AuthPage({ mode, setMode, onSubmit }) {
  const isSignup = mode === "signup";
  const [loginForm, setLoginForm] = React.useState({
    email: "",
    password: ""
  });
  const [signupForm, setSignupForm] = React.useState({
    username: "",
    email: "",
    password: ""
  });
  const form = isSignup ? signupForm : loginForm;

  function update(event) {
    const { name, value } = event.target;
    const setForm = isSignup ? setSignupForm : setLoginForm;
    setForm((current) => ({ ...current, [name]: value }));
  }

  function submit(event) {
    event.preventDefault();
    onSubmit(mode, form);
  }

  function changeMode(nextMode) {
    setMode(nextMode);
  }

  return (
    <main className="auth-page">
      <section className="auth-visual">
        <img src={img.detailHero} alt="Private suite corridor" />
        <div className="auth-visual-copy">
          <p className="eyebrow">Private Access</p>
          <h1>{isSignup ? <>Begin Your <em>Roomly</em> Journey</> : <>Return to Your <em>Roomly</em> Stays</>}</h1>
          <p>Access reservations, protected booking actions, and the manager console through one refined Roomly session.</p>
        </div>
        <div className="auth-badge"><ShieldCheck size={17} /> JWT Protected</div>
      </section>

      <section className="auth-panel">
        <div className="auth-switch">
          <button className={!isSignup ? "active" : ""} onClick={() => changeMode("login")} type="button">Sign In</button>
          <button className={isSignup ? "active" : ""} onClick={() => changeMode("signup")} type="button">Create Account</button>
        </div>

        <form className="auth-form" key={mode} onSubmit={submit}>
          <p className="eyebrow">{isSignup ? "New Guest" : "Welcome Back"}</p>
          <h2>{isSignup ? <>Create Your <em>Account</em></> : <>Sign In to <em>Continue</em></>}</h2>
          <p className="auth-copy">
            {isSignup
              ? "Your account starts with guest access, then unlocks protected booking flows after login."
              : "Use the credentials registered with Roomly to continue into your booking workspace."}
          </p>

          {isSignup && (
            <Field icon={UserRound} label="Username">
              <input
                name="username"
                value={form.username}
                onChange={update}
                placeholder="aamir_roomly"
                required
              />
            </Field>
          )}

          <Field icon={UserRound} label="Email">
            <input
              name="email"
              value={form.email}
              onChange={update}
              placeholder="you@roomly.dev"
              required
              type="email"
            />
          </Field>

          <Field icon={LockKeyhole} label="Password">
            <input
              name="password"
              value={form.password}
              onChange={update}
              placeholder={isSignup ? "At least 10 characters" : "Your password"}
              required
              type="password"
            />
          </Field>

          <button className="forest-button auth-submit" type="submit">
            {isSignup ? "Create Account" : "Sign In"} <ArrowRight size={16} />
          </button>

          <div className="auth-footnote">
            <ShieldCheck size={15} />
            <span>{isSignup ? "Public signup creates a guest account. Manager accounts are issued by Roomly administrators." : "Access tokens are stored locally for protected API calls."}</span>
          </div>
        </form>
      </section>
    </main>
  );
}

function Landing({ query, updateQuery, searchHotels, openHotel }) {
  return (
    <main>
      <section className="landing-hero">
        <div className="hero-copy">
          <p className="eyebrow">Curated stays</p>
          <h1>Find Your <em>Perfect</em> Stay</h1>
          <p>
            Experience the pinnacle of hospitality with live inventory, dynamic pricing, and a booking flow designed to feel like a private concierge.
          </p>
          <SearchBar query={query} updateQuery={updateQuery} onSubmit={searchHotels} />
          <div className="stats-row">
            <Stat value="2,400+" label="Hotels" />
            <Stat value="180+" label="Cities" />
            <Stat value="98%" label="Satisfaction" />
          </div>
        </div>
        <div className="hero-visual">
          <div className="building-mark">ROOMLY</div>
          <ImagePanel image={img.heroLeft} title="Kyoto" subtitle="Garden Suite" />
          <ImagePanel image={img.heroRight} title="Paris" subtitle="Grand Hotel" tall />
          <div className="glass-rating">4.9 <Star size={15} fill="currentColor" /></div>
        </div>
      </section>

      <section className="section editorial">
        <SectionHeader eyebrow="Discover" title={<>Featured <em>Collections</em></>} />
        <div className="featured-grid">
          {fallbackHotels.slice(0, 3).map((hotel) => (
            <HotelCard hotel={hotel} key={hotel.id} onOpen={openHotel} />
          ))}
        </div>
      </section>

      <HowItWorks />
      <RoomTypes />
      <Testimonials />
    </main>
  );
}

function SearchBar({ query, updateQuery, onSubmit, compact = false }) {
  return (
    <form className={compact ? "searchbar compact" : "searchbar"} onSubmit={onSubmit}>
      <Field icon={MapPin} label="Destination">
        <input name="city" value={query.city} onChange={updateQuery} placeholder="Where to next?" />
      </Field>
      <Field icon={CalendarDays} label="Check-in">
        <input name="checkInDate" value={query.checkInDate} onChange={updateQuery} type="date" />
      </Field>
      <Field icon={CalendarDays} label="Check-out">
        <input name="checkOutDate" value={query.checkOutDate} onChange={updateQuery} type="date" />
      </Field>
      <Field icon={BedDouble} label="Rooms">
        <input min="1" name="numberOfRooms" value={query.numberOfRooms} onChange={updateQuery} type="number" />
      </Field>
      <button className="gold-button" type="submit">
        <Search size={18} />
      </button>
    </form>
  );
}

function Field({ icon: Icon, label, children }) {
  return (
    <label className="field">
      <span>{label}</span>
      <div>
        <Icon size={17} />
        {children}
      </div>
    </label>
  );
}

function Stat({ value, label }) {
  return (
    <div className="stat">
      <strong>{value}</strong>
      <span>{label}</span>
    </div>
  );
}

function ImagePanel({ image, title, subtitle, tall }) {
  return (
    <article className={tall ? "image-panel tall" : "image-panel"}>
      <img src={image} alt={`${title} ${subtitle}`} />
      <div>
        <strong>{title}</strong>
        <span>{subtitle}</span>
      </div>
    </article>
  );
}

function SectionHeader({ eyebrow, title, inverse = false }) {
  return (
    <div className={inverse ? "section-header inverse" : "section-header"}>
      <p className="eyebrow">{eyebrow}</p>
      <h2>{title}</h2>
    </div>
  );
}

function HotelCard({ hotel, onOpen }) {
  return (
    <article className="hotel-card">
      <div className="hotel-image">
        <img src={firstPhoto(hotel, img.searchA)} alt={`${hotel.name || hotel.city} hotel`} />
        <span className="availability">Available Stay</span>
        <button className="wish" type="button" aria-label="Wishlist"><Heart size={18} /></button>
      </div>
      <div className="hotel-body">
        <p className="pin"><MapPin size={14} /> {hotel.address || hotel.city}</p>
        <div className="hotel-title-line">
          <h3>{hotel.name || `Roomly ${hotel.city}`}</h3>
          <span className="rating"><Star size={14} fill="currentColor" /> {hotel.rating || 4.9}</span>
        </div>
        <div className="pill-row">
          {(hotel.amenities || []).slice(0, 3).map((amenity) => <span key={amenity}>{amenity}</span>)}
        </div>
        <div className="price-action">
          <p>{money(hotel.estimatedStartingPrice)} <span>/night</span></p>
          <button className="outline-button" onClick={() => onOpen(hotel)} type="button">Book Now</button>
        </div>
      </div>
    </article>
  );
}

function HowItWorks() {
  const steps = [
    ["01", Search, "Curated Selection", "Browse live availability from hotels configured in your backend."],
    ["02", CalendarDays, "Instant Booking", "Choose dates, room count, and rooms with availability checks."],
    ["03", ConciergeBell, "Dynamic Pricing", "Urgency, occupancy, surge, and holidays shape the quote."],
    ["04", ShieldCheck, "Inventory Lock", "Pessimistic locking protects reservations from double booking."]
  ];

  return (
    <section className="how">
      <div className="watermark">ROOMLY</div>
      <SectionHeader eyebrow="The experience" title={<>How Roomly <em>Works</em></>} inverse />
      <div className="steps-grid">
        {steps.map(([number, Icon, title, text]) => (
          <article key={number}>
            <span>{number}</span>
            <div><Icon size={26} /></div>
            <h3>{title}</h3>
            <p>{text}</p>
          </article>
        ))}
      </div>
    </section>
  );
}

function RoomTypes() {
  const cards = [
    ["The Presidential Suite", "Unparalleled Luxury at New Heights", "INR 18,400 / night", img.roomB, "large"],
    ["The Heritage Room", "Classic Elegance", "INR 8,200 / night", img.roomA, ""],
    ["The Garden Terrace", "Nature Infused", "INR 12,800 / night", img.roomC, ""]
  ];

  return (
    <section className="room-types">
      <div className="room-types-head">
        <SectionHeader eyebrow="Refined spaces" title={<>Signature <em>Living</em></>} />
        <div className="tabs">
          {["All", "Standard", "Deluxe", "Suite"].map((tab, index) => <button className={index === 0 ? "active" : ""} key={tab}>{tab}</button>)}
        </div>
      </div>
      <div className="asymmetric-grid">
        {cards.map(([label, title, price, image, size]) => (
          <article className={size === "large" ? "room-type-card large" : "room-type-card"} key={label}>
            <img src={image} alt={label} />
            <div>
              <span>{label}</span>
              <h3>{title}</h3>
              <p>{price}</p>
            </div>
          </article>
        ))}
      </div>
    </section>
  );
}

function Testimonials() {
  const items = [
    ["Eliza Montgomery", "Mumbai", img.avatarA, "Roomly transformed our anniversary trip into a cinematic experience. Every detail felt intentional."],
    ["Julian Vance", "Delhi", img.avatarB, "The booking flow and curated room choices feel calmer and richer than any standard booking app."],
    ["Sara David", "Goa", img.avatarC, "From the first search to the final quote, the whole stay felt thoughtfully composed."]
  ];

  return (
    <section className="testimonials">
      <SectionHeader eyebrow="Guest stories" title={<>Voices of <em>Roomly</em></>} />
      <div className="testimonial-grid">
        {items.map(([name, city, avatar, quote]) => (
          <article key={name}>
            <span className="quote-mark">"</span>
            <div className="stars">{Array.from({ length: 5 }).map((_, i) => <Star key={i} size={14} fill="currentColor" />)}</div>
            <p>{quote}</p>
            <div className="author">
              <img src={avatar} alt={name} />
              <div>
                <strong>{name}</strong>
                <span>{city}</span>
              </div>
            </div>
          </article>
        ))}
      </div>
    </section>
  );
}

function SearchResults({ hotels, query, updateQuery, searchHotels, openHotel }) {
  return (
    <main className="search-page">
      <SearchBar compact query={query} updateQuery={updateQuery} onSubmit={searchHotels} />
      <div className="search-layout">
        <aside className="filters">
          <h3>Refine Search</h3>
          <FilterBlock label="Location">
            <select defaultValue={query.city}>
              <option>Mumbai</option>
              <option>Goa</option>
              <option>Jaipur</option>
              <option>Delhi</option>
            </select>
          </FilterBlock>
          <FilterBlock label="Price per night">
            <div className="range-copy"><span>INR 2,000</span><strong>INR 50,000</strong></div>
            <input type="range" min="2000" max="50000" defaultValue="18000" />
          </FilterBlock>
          <FilterBlock label="Star Rating">
            {[5, 4].map((count) => (
              <label className="check-line" key={count}>
                <input type="checkbox" defaultChecked />
                <span>{Array.from({ length: count }).map((_, i) => <Star key={i} size={13} fill="currentColor" />)}</span>
              </label>
            ))}
          </FilterBlock>
          <FilterBlock label="Amenities">
            {[
              [Wifi, "WiFi"],
              [Waves, "Pool"],
              [Utensils, "Breakfast"],
              [Dumbbell, "Gym"]
            ].map(([Icon, label]) => (
              <label className="check-line" key={label}>
                <input type="checkbox" />
                <span><Icon size={15} /> {label}</span>
              </label>
            ))}
          </FilterBlock>
          <div className="membership-card">
            <img src={img.bath} alt="Luxury concierge desk" />
            <div>
              <span>Membership</span>
              <strong>Unlock exclusive member rates</strong>
            </div>
          </div>
        </aside>
        <section className="results">
          <div className="results-head">
            <div>
              <h1>Showing {hotels.length || 0} Hotels <em>in {query.city}</em></h1>
              <p>Curated selections for the discerning traveler.</p>
            </div>
            <label className="sort-control">
              <span>Sort by:</span>
              <select defaultValue="Recommended">
                <option>Recommended</option>
                <option>Price: Low to High</option>
                <option>Price: High to Low</option>
                <option>Star Rating</option>
              </select>
            </label>
          </div>
          <div className="results-grid">
            {hotels.map((hotel) => <HotelCard hotel={hotel} key={hotel.id} onOpen={openHotel} />)}
          </div>
          <div className="pagination">
            <button><ChevronLeft size={18} /></button>
            <button className="active">1</button>
            <button>2</button>
            <button>3</button>
            <span>...</span>
            <button>12</button>
            <button><ChevronRight size={18} /></button>
          </div>
        </section>
      </div>
    </main>
  );
}

function FilterBlock({ label, children }) {
  return (
    <div className="filter-block">
      <label>{label}</label>
      {children}
    </div>
  );
}

function HotelDetail({ hotel, rooms, query, navigate, selectRoom, authUser }) {
  const amenities = ["Luxury Spa", "Michelin Dining", "Indoor Pool", "Valet Parking", "Private Gym"];
  const bookingBlocked = authUser && !isGuestUser(authUser);
  return (
    <main className="detail-page">
      <button className="back-link" onClick={() => navigate("hotels")}><ChevronLeft size={18} /> Hotels</button>
      <header className="detail-header">
        <div>
          <h1>{hotel?.name || `Roomly ${hotel?.city}`}</h1>
          <p><MapPin size={15} /> {hotel?.address || hotel?.city}</p>
        </div>
        <div className="detail-rating">
          <span>Exceptional</span>
          <div>{Array.from({ length: 5 }).map((_, i) => <Star key={i} size={18} fill="currentColor" />)}</div>
        </div>
      </header>
      <section className="photo-mosaic">
        <img className="main" src={firstPhoto(hotel, img.detailHero)} alt="Hotel suite" />
        <img src={img.bath} alt="Hotel bath" />
        <div>
          <img src={img.breakfast} alt="Breakfast tray" />
          <div className="more-photos">
            <img src={img.pool} alt="Indoor pool" />
            <span>+12 Photos</span>
          </div>
        </div>
      </section>
      <div className="amenity-row">
        {amenities.map((amenity) => <span key={amenity}><Sparkles size={16} /> {amenity}</span>)}
      </div>
      <section className="detail-layout">
        <div className="room-selection">
          <h2>Available <em>Accommodations</em></h2>
          {bookingBlocked && (
            <div className="role-callout">
              <ShieldCheck size={18} />
              <span>Manager sessions can inspect rooms, but reservations require a separate guest account.</span>
            </div>
          )}
          {rooms.map((room, index) => <RoomSelectionCard key={room.id} room={room} featured={index === 1} onSelect={selectRoom} disabled={bookingBlocked} />)}
        </div>
        <aside className="reservation-summary">
          <h3>Reservation Summary</h3>
          <div>
            <span>Stay Dates</span>
            <strong>{query.checkInDate} - {query.checkOutDate}</strong>
          </div>
          <div>
            <span>Room Selected</span>
            <strong>Not selected yet</strong>
          </div>
          <div className="summary-total">
            <span>Estimated Total</span>
            <strong>INR 0</strong>
          </div>
          <button className="gold-solid" disabled>Proceed to Checkout</button>
          <p>Free cancellation until 48 hours before check-in.</p>
        </aside>
      </section>
    </main>
  );
}

function RoomSelectionCard({ room, featured, onSelect, disabled }) {
  return (
    <article className={featured ? "selection-card featured" : "selection-card"}>
      <img src={firstPhoto(room, img.roomA)} alt={room.type} />
      <div>
        <div className="selection-title">
          <div>
            {featured && <span className="exclusive">Most Exclusive</span>}
            <h3>{room.type}</h3>
          </div>
          <p>{money(room.estimatedAverageNightlyPrice || room.basePrice)} <span>per night</span></p>
        </div>
        <p className="room-desc">Elegant proportions, soft light, and the quiet confidence of a stay designed for arrival.</p>
        <div className="room-features">
          <span><UserRound size={15} /> {room.capacity || 2} Guests</span>
          <span><Wifi size={15} /> Free WiFi</span>
          {(room.amenities || []).slice(0, 2).map((amenity) => <span key={amenity}><Sparkles size={15} /> {amenity}</span>)}
        </div>
        <button className="forest-button" onClick={() => onSelect(room)} disabled={disabled}>
          {disabled ? "Guest Account Required" : "Select Room"}
        </button>
      </div>
    </article>
  );
}

function CheckoutModal({ room, hotel, query, onClose, onConfirm }) {
  const [form, setForm] = React.useState({
    checkInDate: query.checkInDate,
    checkOutDate: query.checkOutDate,
    rooms: query.numberOfRooms,
    guests: 2,
    specialRequests: ""
  });
  const [submitting, setSubmitting] = React.useState(false);

  const stayNights = nights({ checkInDate: form.checkInDate, checkOutDate: form.checkOutDate });
  const roomRate = Number(room?.estimatedAverageNightlyPrice || room?.basePrice || 0);
  const subtotal = roomRate * stayNights * Number(form.rooms || 1);
  const gst = subtotal * 0.18;
  const total = subtotal + gst;

  function update(event) {
    const { name, value } = event.target;
    setForm((current) => ({ ...current, [name]: name === "specialRequests" ? value : value }));
  }

  async function submitPayment() {
    setSubmitting(true);
    try {
      await onConfirm({ ...form, finalCalculatedPrice: total });
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="checkout-overlay">
      <div className="checkout-backdrop" onClick={onClose} />
      <section className="checkout-modal">
        <button className="modal-close" onClick={onClose}><X size={24} /></button>
        <div className="checkout-form">
          <p className="eyebrow">Reservation Details</p>
          <h2>Your Stay at <em>Roomly</em></h2>
          <div className="form-grid">
            <Field icon={CalendarDays} label="Check-in date">
              <input name="checkInDate" value={form.checkInDate} onChange={update} type="date" />
            </Field>
            <Field icon={CalendarDays} label="Check-out date">
              <input name="checkOutDate" value={form.checkOutDate} onChange={update} type="date" />
            </Field>
            <Field icon={BedDouble} label="Rooms">
              <input min="1" name="rooms" value={form.rooms} onChange={update} type="number" />
            </Field>
            <Field icon={Users} label="Guests">
              <input min="1" name="guests" value={form.guests} onChange={update} type="number" />
            </Field>
          </div>
          <label className="request-box">
            <span>Special Requests</span>
            <textarea name="specialRequests" value={form.specialRequests} onChange={update} placeholder="Dietary requirements, late check-in, or room scent preferences..." />
          </label>
          <p className="secure-line"><ShieldCheck size={15} /> Guaranteed secure reservation</p>
        </div>
        <aside className="price-panel">
          <h3>Reservation Summary</h3>
          <Line label={`${room?.type || "Selected room"} x ${stayNights} nights`} value={money(subtotal)} />
          <Line label="GST (18%)" value={money(gst)} muted />
          <div className="checkout-total">
            <span>Total Amount</span>
            <strong>{money(total)}</strong>
          </div>
          <button className="gold-solid" onClick={submitPayment} disabled={submitting}>
            {submitting ? "Opening Razorpay..." : "Pay with Razorpay"}
          </button>
          <p>By confirming, you agree to Roomly's editorial cancellation policy.</p>
        </aside>
      </section>
    </div>
  );
}

function Line({ label, value, muted }) {
  return (
    <div className={muted ? "price-line muted" : "price-line"}>
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  );
}

function MyBookings({ bookings, navigate }) {
  return (
    <main className="bookings-page">
      <header className="bookings-head">
        <h1>Your <em>Stays</em> with Roomly</h1>
        <p>A curated collection of past adventures and upcoming journeys.</p>
      </header>
      <div className="booking-tabs">
        {["Upcoming", "Past", "Cancelled"].map((tab, i) => <button className={i === 0 ? "active" : ""} key={tab}>{tab}</button>)}
      </div>
      <section className="booking-list">
        {bookings.map((booking) => <BookingCard booking={booking} key={booking.id} />)}
      </section>
      <section className="escape-card">
        <BedDouble size={42} />
        <h3>Planning your next escape?</h3>
        <p>Discover the newest editorial stays curated for the season.</p>
        <button className="outline-button" onClick={() => navigate("hotels")}>Explore Destinations</button>
      </section>
    </main>
  );
}

function BookingCard({ booking }) {
  return (
    <article className="booking-card">
      <img src={booking.photo || img.heroLeft} alt={booking.hotelName} />
      <div>
        <div className="booking-top">
          <div>
            <h2>{booking.hotelName || "Roomly Stay"}</h2>
            <p><MapPin size={14} /> {booking.city}</p>
          </div>
          <StatusBadge status={booking.status} />
        </div>
        <div className="booking-meta">
          <div><span>Room Type</span><strong>{booking.roomType}</strong></div>
          <div><span>Date Range</span><strong>{booking.checkInDate} - {booking.checkoutDate}</strong></div>
        </div>
        <div className="booking-bottom">
          <span>Booking ID: #{booking.id}</span>
          <strong>{money(booking.finalCalculatedPrice)}</strong>
        </div>
      </div>
    </article>
  );
}

function StatusBadge({ status }) {
  const key = (status || "RESERVED").toUpperCase();
  const label = key === "GUESTS_ADDED" ? "Confirmed" : key.charAt(0) + key.slice(1).toLowerCase();
  return <span className={`status ${key.toLowerCase()}`}>{label}</span>;
}

const blankHotelForm = {
  hotelName: "",
  city: "",
  completeAddress: "",
  location: "",
  email: "",
  phoneNumber: "",
  amenities: "",
  photos: ""
};

const blankRoomForm = {
  type: "",
  basePrice: "",
  totalCount: 1,
  capacity: 2,
  amenities: "",
  photos: ""
};

const blankInventoryForm = {
  roomId: "",
  date: "",
  totalCount: "",
  bookedCount: "",
  surgeFactor: "",
  closed: "false"
};

function AdminDashboard({ authUser }) {
  const [tab, setTab] = React.useState("overview");
  const [hotels, setHotels] = React.useState([]);
  const [rooms, setRooms] = React.useState([]);
  const [managerBookings, setManagerBookings] = React.useState([]);
  const [selectedHotelId, setSelectedHotelId] = React.useState("");
  const [loading, setLoading] = React.useState(false);
  const [message, setMessage] = React.useState("");
  const [bookingFilters, setBookingFilters] = React.useState({
    startDate: "",
    endDate: "",
    finalStatus: ""
  });

  const selectedHotel = hotels.find((hotel) => String(hotel.id) === String(selectedHotelId));

  function showMessage(nextMessage) {
    setMessage(nextMessage);
    window.setTimeout(() => setMessage(""), 3200);
  }

  async function loadHotels() {
    setLoading(true);
    try {
      const hotelList = await request("/api/v1/admin/roomly/hotels");
      setHotels(hotelList || []);
      if (!selectedHotelId && hotelList?.[0]?.id) setSelectedHotelId(String(hotelList[0].id));
    } catch {
      showMessage("Could not load manager hotels. Sign in with manager permissions and retry.");
    } finally {
      setLoading(false);
    }
  }

  async function loadRooms(hotelId = selectedHotelId) {
    if (!hotelId) return;
    setLoading(true);
    try {
      setRooms(await request(`/api/v1/admin/hotels/${hotelId}/rooms`) || []);
    } catch {
      setRooms([]);
      showMessage("Could not load rooms for this hotel.");
    } finally {
      setLoading(false);
    }
  }

  async function loadBookings(hotelId = selectedHotelId, filters = bookingFilters) {
    if (!hotelId) return;
    const params = new URLSearchParams({ hotelId });
    if (filters.startDate) params.set("startDate", filters.startDate);
    if (filters.endDate) params.set("endDate", filters.endDate);
    if (filters.finalStatus) params.set("finalStatus", filters.finalStatus);

    setLoading(true);
    try {
      setManagerBookings(await request(`/api/v1/admin/bookings?${params.toString()}`) || []);
    } catch {
      setManagerBookings([]);
      showMessage("Could not load bookings for this hotel.");
    } finally {
      setLoading(false);
    }
  }

  React.useEffect(() => {
    loadHotels();
  }, []);

  React.useEffect(() => {
    if (!selectedHotelId) return;
    loadRooms(selectedHotelId);
    loadBookings(selectedHotelId);
  }, [selectedHotelId]);

  return (
    <main className="admin-shell">
      <section className="admin-window">
        <aside className="admin-sidebar">
          <div className="dots"><span /><span /><span /></div>
          <h1>Roomly</h1>
          <p>{roleLabel(authUser)} Console</p>
          {[
            ["overview", LayoutDashboard, "Overview"],
            ["hotels", Hotel, "Hotels"],
            ["rooms", BedDouble, "Rooms"],
            ["bookings", CalendarDays, "Bookings"],
            ["revenue", CircleDollarSign, "Revenue"]
          ].map(([key, Icon, label]) => (
            <button className={tab === key ? "active" : ""} key={key} onClick={() => setTab(key)}>
              <Icon size={19} /> {label}
            </button>
          ))}
          <div className="manager-profile">
            <div>{authUser?.username?.slice(0, 2).toUpperCase() || "RM"}</div>
            <span>{authUser?.username || "Manager Profile"}</span>
            <small>{roleLabel(authUser)}</small>
          </div>
        </aside>
        <section className="admin-main">
          <header className="admin-top">
            <label className="admin-hotel-picker">
              <Building2 size={18} />
              <select value={selectedHotelId} onChange={(event) => setSelectedHotelId(event.target.value)}>
                <option value="">Select hotel</option>
                {hotels.map((hotel) => <option key={hotel.id} value={hotel.id}>{displayHotelName(hotel)} - {hotel.city}</option>)}
              </select>
            </label>
            <div className="admin-actions"><button onClick={loadHotels} type="button"><Bell size={20} /></button><span>AS</span></div>
          </header>
          {loading && <div className="admin-loading">Syncing manager workspace</div>}
          {message && <div className="admin-notice">{message}</div>}
          {tab === "overview" && <AdminOverview hotels={hotels} rooms={rooms} bookings={managerBookings} selectedHotel={selectedHotel} />}
          {tab === "hotels" && <AdminHotels hotels={hotels} refresh={loadHotels} showMessage={showMessage} />}
          {tab === "rooms" && <AdminRooms hotelId={selectedHotelId} rooms={rooms} refresh={() => loadRooms(selectedHotelId)} showMessage={showMessage} />}
          {tab === "bookings" && (
            <AdminBookings
              bookings={managerBookings}
              filters={bookingFilters}
              setFilters={setBookingFilters}
              loadBookings={() => loadBookings(selectedHotelId, bookingFilters)}
              selectedHotel={selectedHotel}
            />
          )}
          {tab === "revenue" && <AdminRevenue />}
        </section>
      </section>
    </main>
  );
}

function AdminOverview({ hotels, rooms, bookings, selectedHotel }) {
  const revenue = bookings.reduce((sum, booking) => sum + Number(booking.finalCalculatedPrice || 0), 0);
  return (
    <div className="admin-content">
      <div className="admin-title">
        <div>
          <h2>Executive <em>Overview</em></h2>
          <p>{selectedHotel ? `Operational data for ${displayHotelName(selectedHotel)}.` : "Select a hotel to review live operations."}</p>
        </div>
      </div>
      <div className="stat-cards">
        <AdminStat icon={CalendarDays} label="Total Bookings" value={bookings.length} trend="Live" />
        <AdminStat icon={BadgeIndianRupee} label="Revenue" value={money(revenue)} dark trend="Booked" />
        <AdminStat icon={Building2} label="Active Hotels" value={hotels.filter((hotel) => hotel.active !== false).length} trend={`${hotels.length} total`} />
        <AdminStat icon={BedDouble} label="Rooms Listed" value={rooms.length} trend="Selected hotel" />
      </div>
      <div className="admin-grid">
        <RecentBookings bookings={bookings.slice(0, 5)} />
        <RevenueCard />
      </div>
    </div>
  );
}

function AdminStat({ icon: Icon, label, value, trend, dark }) {
  return (
    <article className={dark ? "admin-stat dark" : "admin-stat"}>
      <div><Icon size={23} /><span>{trend}</span></div>
      <p>{label}</p>
      <strong>{value}</strong>
    </article>
  );
}

function RecentBookings({ bookings = sampleBookings }) {
  return (
    <div className="table-card">
      <div className="table-head"><h3>Recent <em>Bookings</em></h3><button>View All</button></div>
      <table>
        <thead><tr><th>Guest</th><th>Hotel/Room</th><th>Status</th><th>Amount</th></tr></thead>
        <tbody>
          {bookings.map((booking, index) => (
            <tr key={booking.id}>
              <td><span className="avatar">{booking.guestEmail?.slice(0, 2).toUpperCase() || ["EM", "JL", "SW"][index] || "RM"}</span><div><strong>{booking.guestEmail || ["Eleanor Moore", "Julian Laurent", "Sofia Wood"][index] || "Guest"}</strong><small>{booking.createdAt ? new Date(booking.createdAt).toLocaleDateString() : "Confirmed recently"}</small></div></td>
              <td><strong>{booking.hotelName}</strong><small>{booking.roomType}</small></td>
              <td><StatusBadge status={booking.status} /></td>
              <td>{money(booking.finalCalculatedPrice)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

function RevenueCard() {
  return (
    <aside className="revenue-card">
      <p>Market Performance</p>
      <h3>Growth Forecast</h3>
      <div className="bars">{[40, 65, 85, 50, 70, 100].map((height, i) => <span style={{ height: `${height}%` }} className={i === 2 || i === 5 ? "gold" : ""} key={height + i} />)}</div>
    </aside>
  );
}

function AdminHotels({ hotels, refresh, showMessage }) {
  const [form, setForm] = React.useState(blankHotelForm);
  const [editingId, setEditingId] = React.useState(null);

  function update(event) {
    const { name, value } = event.target;
    setForm((current) => ({ ...current, [name]: value }));
  }

  function editHotel(hotel) {
    setEditingId(hotel.id);
    setForm({
      hotelName: displayHotelName(hotel),
      city: hotel.city || "",
      completeAddress: hotel.contactInfo?.completeAddress || "",
      location: hotel.contactInfo?.location || "",
      email: hotel.contactInfo?.email || "",
      phoneNumber: hotel.contactInfo?.phoneNumber || "",
      amenities: listToCsv(hotel.amenities),
      photos: listToCsv(hotel.photos)
    });
  }

  async function submit(event) {
    event.preventDefault();
    const payload = {
      hotelName: form.hotelName,
      city: form.city,
      contactInfo: {
        completeAddress: form.completeAddress,
        location: form.location,
        email: form.email,
        phoneNumber: form.phoneNumber
      },
      amenities: csvToList(form.amenities),
      photos: csvToList(form.photos)
    };

    try {
      if (editingId) {
        await request(`/api/v1/admin/hotels/${editingId}`, { method: "PATCH", body: JSON.stringify(payload) });
        showMessage("Hotel updated.");
      } else {
        await request("/api/v1/admin/hotels", { method: "POST", body: JSON.stringify(payload) });
        showMessage("Hotel created.");
      }
      setForm(blankHotelForm);
      setEditingId(null);
      refresh();
    } catch {
      showMessage("Hotel save failed. Check required fields and permissions.");
    }
  }

  async function deleteHotel(id) {
    try {
      await request(`/api/v1/admin/hotels/${id}`, { method: "DELETE" });
      showMessage("Hotel deleted.");
      refresh();
    } catch {
      showMessage("Hotel delete failed.");
    }
  }

  return (
    <div className="admin-content">
      <div className="admin-title"><h2>Registered <em>Hotels</em></h2><p>Create, update, and retire hotel listings.</p></div>
      <form className="admin-form" onSubmit={submit}>
        <Field icon={Hotel} label="Hotel name"><input name="hotelName" value={form.hotelName} onChange={update} placeholder="The Emerald Estate" required /></Field>
        <Field icon={MapPin} label="City"><input name="city" value={form.city} onChange={update} placeholder="Mumbai" required /></Field>
        <Field icon={MapPin} label="Address"><input name="completeAddress" value={form.completeAddress} onChange={update} placeholder="Marine Drive" required /></Field>
        <Field icon={Building2} label="Location"><input name="location" value={form.location} onChange={update} placeholder="South Mumbai" required /></Field>
        <Field icon={UserRound} label="Email"><input name="email" value={form.email} onChange={update} placeholder="manager@roomly.dev" required type="email" /></Field>
        <Field icon={Bell} label="Phone"><input name="phoneNumber" value={form.phoneNumber} onChange={update} placeholder="+919999999999" required /></Field>
        <label className="admin-wide"><span>Amenities</span><textarea name="amenities" value={form.amenities} onChange={update} placeholder="Spa, Pool, Breakfast" /></label>
        <label className="admin-wide"><span>Photo URLs</span><textarea name="photos" value={form.photos} onChange={update} placeholder="https://..." /></label>
        <button className="forest-button" type="submit"><Plus size={16} /> {editingId ? "Update Hotel" : "Add Hotel"}</button>
        {editingId && <button className="soft-button" onClick={() => { setEditingId(null); setForm(blankHotelForm); }} type="button">Cancel Edit</button>}
      </form>
      <AdminEntityList
        empty="No hotels returned from backend."
        items={hotels}
        renderTitle={displayHotelName}
        renderMeta={(hotel) => `${hotel.city || "No city"} - ${hotel.active === false ? "Inactive" : "Active"}`}
        onEdit={editHotel}
        onDelete={(hotel) => deleteHotel(hotel.id)}
      />
    </div>
  );
}

function AdminRooms({ hotelId, rooms, refresh, showMessage }) {
  const [form, setForm] = React.useState(blankRoomForm);
  const [inventoryForm, setInventoryForm] = React.useState(blankInventoryForm);
  const [editingId, setEditingId] = React.useState(null);

  function update(event) {
    const { name, value } = event.target;
    setForm((current) => ({ ...current, [name]: value }));
  }

  function editRoom(room) {
    setEditingId(room.id);
    setForm({
      type: room.type || "",
      basePrice: room.basePrice || "",
      totalCount: room.totalCount || 1,
      capacity: room.capacity || 2,
      amenities: listToCsv(room.amenities),
      photos: listToCsv(room.photos)
    });
  }

  async function submit(event) {
    event.preventDefault();
    if (!hotelId) {
      showMessage("Select a hotel before managing rooms.");
      return;
    }
    const payload = {
      type: form.type,
      basePrice: Number(form.basePrice),
      totalCount: Number(form.totalCount),
      capacity: Number(form.capacity),
      amenities: csvToList(form.amenities),
      photos: csvToList(form.photos)
    };

    try {
      if (editingId) {
        await request(`/api/v1/admin/hotels/${hotelId}/rooms/${editingId}`, { method: "PATCH", body: JSON.stringify(payload) });
        showMessage("Room updated.");
      } else {
        await request(`/api/v1/admin/hotels/${hotelId}/rooms`, { method: "POST", body: JSON.stringify(payload) });
        showMessage("Room added.");
      }
      setForm(blankRoomForm);
      setEditingId(null);
      refresh();
    } catch {
      showMessage("Room save failed. Check hotel selection, fields, and permissions.");
    }
  }

  async function deleteRoom(room) {
    try {
      await request(`/api/v1/admin/hotels/${hotelId}/rooms/${room.id}`, { method: "DELETE" });
      showMessage("Room deleted.");
      refresh();
    } catch {
      showMessage("Room delete failed.");
    }
  }

  function updateInventoryForm(event) {
    const { name, value } = event.target;
    setInventoryForm((current) => ({ ...current, [name]: value }));
  }

  async function submitInventory(event) {
    event.preventDefault();
    if (!hotelId || !inventoryForm.roomId || !inventoryForm.date) {
      showMessage("Select hotel, room, and date before updating inventory.");
      return;
    }

    const payload = {
      ...(inventoryForm.totalCount !== "" ? { totalCount: Number(inventoryForm.totalCount) } : {}),
      ...(inventoryForm.bookedCount !== "" ? { bookedCount: Number(inventoryForm.bookedCount) } : {}),
      ...(inventoryForm.surgeFactor !== "" ? { surgeFactor: Number(inventoryForm.surgeFactor) } : {}),
      closed: inventoryForm.closed === "true"
    };

    try {
      await request(`/api/v1/admin/inventory/${hotelId}/${inventoryForm.roomId}/${inventoryForm.date}`, {
        method: "PATCH",
        body: JSON.stringify(payload)
      });
      showMessage("Inventory updated.");
      setInventoryForm(blankInventoryForm);
    } catch {
      showMessage("Inventory update failed. Check date, room, and permissions.");
    }
  }

  return (
    <div className="admin-content">
      <div className="admin-title"><h2>Room <em>Inventory</em></h2><p>{hotelId ? "Manage rooms for the selected hotel." : "Select a hotel to manage rooms."}</p></div>
      <form className="admin-form" onSubmit={submit}>
        <Field icon={BedDouble} label="Room type"><input name="type" value={form.type} onChange={update} placeholder="Executive Suite" required /></Field>
        <Field icon={BadgeIndianRupee} label="Base price"><input name="basePrice" value={form.basePrice} onChange={update} min="1" type="number" required /></Field>
        <Field icon={Building2} label="Total count"><input name="totalCount" value={form.totalCount} onChange={update} min="1" type="number" required /></Field>
        <Field icon={Users} label="Capacity"><input name="capacity" value={form.capacity} onChange={update} min="1" type="number" required /></Field>
        <label className="admin-wide"><span>Amenities</span><textarea name="amenities" value={form.amenities} onChange={update} placeholder="WiFi, Balcony, Breakfast" /></label>
        <label className="admin-wide"><span>Photo URLs</span><textarea name="photos" value={form.photos} onChange={update} placeholder="https://..." /></label>
        <button className="forest-button" type="submit"><Plus size={16} /> {editingId ? "Update Room" : "Add Room"}</button>
        {editingId && <button className="soft-button" onClick={() => { setEditingId(null); setForm(blankRoomForm); }} type="button">Cancel Edit</button>}
      </form>
      <AdminEntityList
        empty="No rooms returned for this hotel."
        items={rooms}
        renderTitle={(room) => room.type}
        renderMeta={(room) => `${money(room.basePrice)} - ${room.capacity || 0} guests`}
        onEdit={editRoom}
        onDelete={deleteRoom}
      />
      <div className="admin-title inventory-title"><h2>Daily <em>Inventory</em></h2><p>Patch availability, closure, and surge pricing for one room/date.</p></div>
      <form className="admin-form inventory-form" onSubmit={submitInventory}>
        <Field icon={BedDouble} label="Room">
          <select name="roomId" value={inventoryForm.roomId} onChange={updateInventoryForm} required>
            <option value="">Select room</option>
            {rooms.map((room) => <option key={room.id} value={room.id}>{room.type}</option>)}
          </select>
        </Field>
        <Field icon={CalendarDays} label="Date"><input name="date" value={inventoryForm.date} onChange={updateInventoryForm} type="date" required /></Field>
        <Field icon={Building2} label="Total count"><input name="totalCount" value={inventoryForm.totalCount} onChange={updateInventoryForm} min="0" type="number" /></Field>
        <Field icon={Users} label="Booked count"><input name="bookedCount" value={inventoryForm.bookedCount} onChange={updateInventoryForm} min="0" type="number" /></Field>
        <Field icon={BadgeIndianRupee} label="Surge factor"><input name="surgeFactor" value={inventoryForm.surgeFactor} onChange={updateInventoryForm} min="0" step="0.01" type="number" placeholder="1.20" /></Field>
        <Field icon={LockKeyhole} label="Closed">
          <select name="closed" value={inventoryForm.closed} onChange={updateInventoryForm}>
            <option value="false">Open</option>
            <option value="true">Closed</option>
          </select>
        </Field>
        <button className="forest-button" type="submit"><Check size={16} /> Update Inventory</button>
      </form>
    </div>
  );
}

function AdminBookings({ bookings, filters, setFilters, loadBookings, selectedHotel }) {
  function update(event) {
    const { name, value } = event.target;
    setFilters((current) => ({ ...current, [name]: value }));
  }

  return (
    <div className="admin-content">
      <div className="admin-title">
        <div><h2>Booking <em>Ledger</em></h2><p>{selectedHotel ? `Bookings for ${displayHotelName(selectedHotel)}.` : "Select a hotel to load bookings."}</p></div>
        <div><button className="soft-button" onClick={loadBookings} type="button"><SlidersHorizontal size={15} /> Apply Filters</button></div>
      </div>
      <div className="booking-filter-row">
        <Field icon={CalendarDays} label="Start date"><input name="startDate" value={filters.startDate} onChange={update} type="date" /></Field>
        <Field icon={CalendarDays} label="End date"><input name="endDate" value={filters.endDate} onChange={update} type="date" /></Field>
        <Field icon={Check} label="Status">
          <select name="finalStatus" value={filters.finalStatus} onChange={update}>
            <option value="">All statuses</option>
            <option value="RESERVED">Reserved</option>
            <option value="GUESTS_ADDED">Guests added</option>
            <option value="CANCELLED">Cancelled</option>
          </select>
        </Field>
      </div>
      <RecentBookings bookings={bookings} />
    </div>
  );
}

function AdminRevenue() {
  const [filters, setFilters] = React.useState({ startDate: "", endDate: "" });
  const [report, setReport] = React.useState(null);
  const [loading, setLoading] = React.useState(false);
  const [error, setError] = React.useState("");

  function update(event) {
    const { name, value } = event.target;
    setFilters((current) => ({ ...current, [name]: value }));
  }

  async function generateReport(event) {
    event.preventDefault();
    const params = new URLSearchParams();
    if (filters.startDate) params.set("startDate", filters.startDate);
    if (filters.endDate) params.set("endDate", filters.endDate);
    setLoading(true);
    setError("");
    try {
      setReport(await request(`/api/v1/admin/reports?${params.toString()}`, { method: "POST" }));
    } catch {
      setReport(null);
      setError("Report generation failed. Sign in with manager permissions and retry.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="admin-content">
      <div className="admin-title"><h2>Revenue <em>Room</em></h2><p>Generate booking reports from the backend reporting API.</p></div>
      <form className="booking-filter-row" onSubmit={generateReport}>
        <Field icon={CalendarDays} label="Start date"><input name="startDate" value={filters.startDate} onChange={update} type="date" /></Field>
        <Field icon={CalendarDays} label="End date"><input name="endDate" value={filters.endDate} onChange={update} type="date" /></Field>
        <button className="forest-button" type="submit"><Download size={16} /> {loading ? "Generating" : "Generate Report"}</button>
      </form>
      {error && <div className="admin-notice inline">{error}</div>}
      {report ? (
        <div className="report-grid">
          <AdminStat icon={CalendarDays} label="Total Bookings" value={report.totalBookings || 0} trend="Report" />
          <AdminStat icon={BadgeIndianRupee} label="Total Revenue" value={money(report.totalRevenue || 0)} dark trend="Report" />
          <RevenueCard />
        </div>
      ) : (
        <RevenueCard />
      )}
    </div>
  );
}

function AdminEntityList({ items, renderTitle, renderMeta, onEdit, onDelete, empty }) {
  return (
    <div className="admin-list">
      {!items.length && <article><span>{empty}</span></article>}
      {items.map((item) => (
        <article key={item.id || renderTitle(item)}>
          <span><strong>{renderTitle(item)}</strong><small>{renderMeta(item)}</small></span>
          <div><button onClick={() => onEdit(item)} type="button"><Edit3 size={16} /></button><button onClick={() => onDelete(item)} type="button"><Trash2 size={16} /></button></div>
        </article>
      ))}
    </div>
  );
}

function checkoutTotal(room, form) {
  const fakeQuery = { checkInDate: form.checkInDate, checkOutDate: form.checkOutDate };
  const subtotal = Number(room?.estimatedAverageNightlyPrice || room?.basePrice || 0) * nights(fakeQuery) * Number(form.rooms || 1);
  return subtotal + subtotal * 0.18;
}

function Toast({ message }) {
  return <div className="toast">{message}</div>;
}

function Footer() {
  return (
    <footer className="footer">
      <div>
        <h2>Roomly</h2>
        <p>Elevating the art of the stay through meticulous curation and live pricing.</p>
      </div>
      {[
        ["Company", "Brand", "Our Story", "Careers", "Press"],
        ["For Guests", "Collections", "Concierge", "Member Perks", "Support"],
        ["For Managers", "List Your Property", "Partner Portal", "Management Tools", "Affiliates"]
      ].map(([title, ...links]) => (
        <div key={title}>
          <h3>{title}</h3>
          {links.map((link) => <a href="#" key={link}>{link}</a>)}
        </div>
      ))}
    </footer>
  );
}

createRoot(document.getElementById("root")).render(<App />);
