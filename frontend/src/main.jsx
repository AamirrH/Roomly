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
import { Landing, SearchResults } from "./components/hotelSearch";
import { AdminDashboard } from "./components/adminDashboard";
import { CheckoutModal, HotelDetail, MyBookings } from "./components/stayExperience";
import { fallbackHotels, fallbackRooms, img, sampleBookings } from "./data/roomlyContent";
import { pageInfo, responseItems } from "./lib/apiShape";
import { csvToList, displayHotelName, firstPhoto, listToCsv, money, nights } from "./lib/display";
import { loadRazorpayCheckout, razorpayFailureMessage } from "./lib/razorpay";
import { apiRequest } from "./lib/request";
import "./styles.css";

const API_BASE = import.meta.env.VITE_API_BASE_URL || "http://localhost:8081";
const ACCESS_TOKEN_KEY = "roomlyAccessToken";
const USER_KEY = "roomlyUser";
const HOTEL_RESULTS_PAGE_SIZE = 6;
const VIEW_HASH = {
  home: "",
  hotels: "hotels",
  detail: "hotel",
  bookings: "bookings",
  manager: "manager",
  auth: "auth"
};
const HASH_VIEW = Object.fromEntries(Object.entries(VIEW_HASH).map(([view, hash]) => [hash, view]));

function viewFromHash() {
  const hash = window.location.hash.replace(/^#\/?/, "").split("?")[0];
  return HASH_VIEW[hash] || "home";
}

function hashForView(view) {
  const hash = VIEW_HASH[view] || "";
  return hash ? `#/${hash}` : "#/";
}

function isoDateAfter(days) {
  const date = new Date();
  date.setDate(date.getDate() + days);
  return date.toISOString().slice(0, 10);
}

function defaultQuery() {
  return {
    city: "",
    checkInDate: isoDateAfter(1),
    checkOutDate: isoDateAfter(4),
    numberOfRooms: 1
  };
}

function isValidDateValue(value) {
  return /^\d{4}-\d{2}-\d{2}$/.test(String(value || ""));
}

function normalizeSearchQuery(searchQuery) {
  const fallback = defaultQuery();
  const checkInDate = isValidDateValue(searchQuery.checkInDate) ? searchQuery.checkInDate : fallback.checkInDate;
  let checkOutDate = isValidDateValue(searchQuery.checkOutDate) ? searchQuery.checkOutDate : fallback.checkOutDate;

  if (checkOutDate <= checkInDate) {
    const nextCheckout = new Date(`${checkInDate}T00:00:00`);
    nextCheckout.setDate(nextCheckout.getDate() + 1);
    checkOutDate = nextCheckout.toISOString().slice(0, 10);
  }

  return {
    ...searchQuery,
    checkInDate,
    checkOutDate,
    numberOfRooms: Math.max(1, Number(searchQuery.numberOfRooms || 1))
  };
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

function request(path, options = {}) {
  return apiRequest(API_BASE, ACCESS_TOKEN_KEY, path, options);
}

function App() {
  const [view, setView] = React.useState(() => viewFromHash());
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
  const [query, setQuery] = React.useState(defaultQuery);
  const [hotels, setHotels] = React.useState(fallbackHotels);
  const [rawHotelResults, setRawHotelResults] = React.useState(fallbackHotels);
  const [hotelResults, setHotelResults] = React.useState(fallbackHotels);
  const [hotelPage, setHotelPage] = React.useState({
    number: 0,
    size: HOTEL_RESULTS_PAGE_SIZE,
    totalPages: 1,
    totalElements: fallbackHotels.length
  });
  const [hotelSort, setHotelSort] = React.useState("recommended");
  const [hotelFilters, setHotelFilters] = React.useState({
    amenities: []
  });
  const [rooms, setRooms] = React.useState(fallbackRooms);
  const [bookings, setBookings] = React.useState(sampleBookings);
  const [selectedHotel, setSelectedHotel] = React.useState(fallbackHotels[0]);
  const [selectedRoom, setSelectedRoom] = React.useState(fallbackRooms[0]);
  const [checkoutOpen, setCheckoutOpen] = React.useState(false);
  const [cancelTarget, setCancelTarget] = React.useState(null);
  const [toast, setToast] = React.useState("");
  const [loading, setLoading] = React.useState(false);
  const hotelSearchCache = React.useRef(new Map());

  React.useEffect(() => {
    if (!window.location.hash) {
      window.history.replaceState({ view: "home" }, "", hashForView("home"));
    } else {
      window.history.replaceState({ view }, "", hashForView(view));
    }

    function handleHistoryChange() {
      setView(viewFromHash());
      setMenuOpen(false);
      setCheckoutOpen(false);
      setCancelTarget(null);
    }

    window.addEventListener("popstate", handleHistoryChange);
    return () => window.removeEventListener("popstate", handleHistoryChange);
  }, []);

  function goToView(next, options = {}) {
    setView(next);
    const nextHash = hashForView(next);
    if (window.location.hash !== nextHash) {
      const method = options.replace ? "replaceState" : "pushState";
      window.history[method]({ view: next }, "", nextHash);
    }
  }

  function notify(message) {
    setToast(message);
    window.setTimeout(() => setToast(""), 2800);
  }

  function openAuth(mode = "login") {
    setAuthMode(mode);
    goToView("auth");
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
            password: form.password,
            role: form.role
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
      notify(mode === "signup" ? `Welcome to Roomly. Your ${isManagerUser(nextUser) ? "manager" : "guest"} account is ready.` : "Welcome back. Your session is ready.");
      goToView(isManagerUser(nextUser) ? "manager" : "home", { replace: true });
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
    goToView("home", { replace: true });
  }

  function updateQuery(event) {
    const { name, value } = event.target;
    setQuery((current) => ({
      ...current,
      [name]: name === "numberOfRooms" ? Number(value) : value
    }));
  }

  function hotelSearchCacheKey(searchQuery, pageNumber, pageSize) {
    return JSON.stringify({
      city: searchQuery.city?.trim() || "",
      checkInDate: searchQuery.checkInDate,
      checkOutDate: searchQuery.checkOutDate,
      numberOfRooms: Number(searchQuery.numberOfRooms || 1),
      pageNumber,
      pageSize
    });
  }

  function priceValue(hotel) {
    const value = Number(hotel?.estimatedStartingPrice);
    return Number.isFinite(value) && value > 0 ? value : null;
  }

  function amenityMatches(hotelAmenities = [], selectedAmenity) {
    const needle = selectedAmenity.toLowerCase();
    return hotelAmenities.some((amenity) => String(amenity).toLowerCase().includes(needle));
  }

  function filterHotelList(hotelList, filters = hotelFilters) {
    return hotelList.filter((hotel) => {
      const amenitiesMatch = !filters.amenities.length || filters.amenities.every((amenity) => amenityMatches(hotel.amenities || [], amenity));

      return amenitiesMatch;
    });
  }

  function sortHotelList(hotelList, sortMode = hotelSort) {
    const copy = [...hotelList];

    if (sortMode === "priceLowHigh") {
      return copy.sort((a, b) => {
        const aPrice = priceValue(a);
        const bPrice = priceValue(b);
        if (aPrice === null) return 1;
        if (bPrice === null) return -1;
        return aPrice - bPrice;
      });
    }

    if (sortMode === "priceHighLow") {
      return copy.sort((a, b) => {
        const aPrice = priceValue(a);
        const bPrice = priceValue(b);
        if (aPrice === null) return 1;
        if (bPrice === null) return -1;
        return bPrice - aPrice;
      });
    }

    if (sortMode === "name") {
      return copy.sort((a, b) => displayHotelName(a).localeCompare(displayHotelName(b)));
    }

    return copy;
  }

  function applyHotelResults(pageHotels, metadata, sortMode = hotelSort, filters = hotelFilters) {
    const filteredHotels = filterHotelList(pageHotels, filters);
    const sortedHotels = sortHotelList(filteredHotels, sortMode);
    const nextMetadata = {
      ...metadata,
      totalElements: filters.amenities.length ? sortedHotels.length : metadata.totalElements,
      totalPages: filters.amenities.length ? 1 : metadata.totalPages
    };
    setRawHotelResults(pageHotels);
    setHotelResults(sortedHotels);
    setHotels(sortedHotels);
    setHotelPage(nextMetadata);
    return nextMetadata;
  }

  async function fetchHotelPage(pageNumber, pageSize, searchQuery = query) {
    const safeQuery = normalizeSearchQuery(searchQuery);
    const params = new URLSearchParams({
      checkInDate: safeQuery.checkInDate,
      checkOutDate: safeQuery.checkOutDate,
      numberOfRooms: safeQuery.numberOfRooms,
      pageNumber,
      pageSize
    });
    if (safeQuery.city?.trim()) {
      params.set("city", safeQuery.city.trim());
    }

    const page = await request(`/roomly/api/v1/hotels/search?${params.toString()}`);
    const content = responseItems(page);
    return {
      content,
      metadata: pageInfo(page, { number: pageNumber, size: pageSize, totalElements: content.length, totalPages: 1 })
    };
  }

  async function searchHotels(event, pageNumber = 0, pageSize = HOTEL_RESULTS_PAGE_SIZE, searchQuery = query) {
    event?.preventDefault();
    const safeQuery = normalizeSearchQuery(searchQuery);
    setQuery(safeQuery);
    goToView("hotels");
    const cacheKey = hotelSearchCacheKey(safeQuery, pageNumber, pageSize);
    const cachedPage = hotelSearchCache.current.get(cacheKey);

    if (cachedPage) {
      applyHotelResults(cachedPage.content, cachedPage.metadata);
      setMenuOpen(false);
      return;
    }

    setLoading(true);
    setHotels([]);
    setRawHotelResults([]);
    setHotelResults([]);
    setHotelPage({
      number: 0,
      size: pageSize,
      totalPages: 1,
      totalElements: 0
    });
    try {
      const requestedPage = await fetchHotelPage(pageNumber, pageSize, safeQuery);
      hotelSearchCache.current.set(cacheKey, requestedPage);
      const metadata = applyHotelResults(requestedPage.content, requestedPage.metadata);

      if (import.meta.env.DEV) {
        console.info("Roomly hotel search page", {
          requestedPage: pageNumber,
          returnedItems: requestedPage.content.length,
          metadata
        });
      }
      if (!requestedPage.content.length) notify("No live stays returned for these dates.");
    } catch (error) {
      setRawHotelResults(fallbackHotels);
      setHotelResults(fallbackHotels);
      setHotels(fallbackHotels);
      setHotelPage({
        number: 0,
        size: HOTEL_RESULTS_PAGE_SIZE,
        totalPages: Math.max(Math.ceil(fallbackHotels.length / HOTEL_RESULTS_PAGE_SIZE), 1),
        totalElements: fallbackHotels.length
      });
      notify("Backend request failed. Showing editorial sample stays.");
    } finally {
      setLoading(false);
    }
  }

  function changeHotelPage(nextPage) {
    const boundedPage = Math.max(0, Math.min(nextPage, Math.max(hotelPage.totalPages - 1, 0)));
    const pageSize = hotelPage.size || HOTEL_RESULTS_PAGE_SIZE;
    searchHotels(null, boundedPage, pageSize);
  }

  function changeHotelSort(nextSort) {
    setHotelSort(nextSort);
    setLoading(true);

    window.setTimeout(() => {
      applyHotelResults(rawHotelResults, hotelPage, nextSort, hotelFilters);
      setLoading(false);
    }, 240);
  }

  function changeHotelFilters(nextFilters) {
    const mergedFilters = {
      ...hotelFilters,
      ...nextFilters
    };
    setHotelFilters(mergedFilters);
    setLoading(true);

    window.setTimeout(() => {
      applyHotelResults(rawHotelResults, hotelPage, hotelSort, mergedFilters);
      setLoading(false);
    }, 240);
  }

  function applyRefineSearch(nextQuery) {
    setQuery(nextQuery);
    searchHotels(null, 0, HOTEL_RESULTS_PAGE_SIZE, nextQuery);
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
      setRooms(roomContent.length ? roomContent : fallbackRooms.map((room) => ({ ...room, hotelId: hotel.id })));
      if (!roomContent.length) notify("No live rooms returned yet. Showing sample room selections.");
    } catch (error) {
      setRooms(fallbackRooms.map((room) => ({ ...room, hotelId: hotel.id })));
      notify("Room request failed. Showing sample room selections.");
    } finally {
      goToView("detail");
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

  async function cancelBooking(booking) {
    if (!booking?.id || Number.isNaN(Number(booking.id))) {
      notify("Demo bookings cannot be cancelled. Try this on a live booking.");
      return;
    }

    setCancelTarget(booking);
  }

  async function confirmCancelBooking() {
    if (!cancelTarget) return;
    setLoading(true);
    try {
      await request(`/roomly/api/v1/bookings/${cancelTarget.id}/cancel`, {
        method: "PATCH"
      });
      setBookings((current) => current.map((item) => (
        item.id === cancelTarget.id ? { ...item, status: "CANCELLED" } : item
      )));
      setCancelTarget(null);
      notify("Booking cancelled successfully.");
    } catch (error) {
      notify(error?.message || "Could not cancel this booking.");
    } finally {
      setLoading(false);
    }
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

      const bookingWithGuests = await request(`/roomly/api/v1/bookings/${created.id}/guests`, {
        method: "PATCH",
        body: JSON.stringify(form.guestList)
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
          ...bookingWithGuests,
          status: verifiedPayment?.bookingStatus || "CONFIRMED",
          id: created?.id || `RM-${Date.now()}`,
          hotelName: displayHotelName(selectedHotel),
          roomType: selectedRoom.type,
          city: selectedHotel.city,
          photo: firstPhoto(selectedRoom, img.roomA)
        },
        ...current
      ]);
      notify("Payment verified. Your booking is confirmed.");
      setCheckoutOpen(false);
      goToView("bookings");
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
      if (!paymentOrder?.razorpayOrderId || !paymentOrder?.razorpayKeyId || !paymentOrder?.amountInPaise || !paymentOrder?.currency) {
        console.error("Invalid Razorpay order payload", paymentOrder);
        reject(new Error("Invalid Razorpay order payload from backend"));
        return;
      }

      console.info("Opening Razorpay checkout", {
        keyPrefix: paymentOrder.razorpayKeyId.slice(0, 8),
        orderPrefix: paymentOrder.razorpayOrderId.slice(0, 8),
        amountInPaise: paymentOrder.amountInPaise,
        currency: paymentOrder.currency
      });

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
        console.warn("Razorpay payment failed", response?.error);
        reject(new Error(razorpayFailureMessage(response)));
      });

      checkout.open();
    });
  }

  function navigate(next) {
    if (next === "hotels") {
      setMenuOpen(false);
      searchHotels(null, 0, HOTEL_RESULTS_PAGE_SIZE);
      return;
    }
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
    goToView(next);
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
      />
      {loading && <div className="loading-strip">Curating your request</div>}
      {view === "home" && <Landing query={query} updateQuery={updateQuery} searchHotels={searchHotels} openHotel={openHotel} />}
      {view === "hotels" && <SearchResults hotels={hotels} page={hotelPage} query={query} updateQuery={updateQuery} searchHotels={searchHotels} applyRefineSearch={applyRefineSearch} changePage={changeHotelPage} changeSort={changeHotelSort} selectedSort={hotelSort} filters={hotelFilters} changeFilters={changeHotelFilters} openHotel={openHotel} loading={loading} />}
      {view === "detail" && <HotelDetail hotel={selectedHotel} rooms={rooms} query={query} navigate={navigate} selectRoom={selectRoom} bookingBlocked={authUser && !isGuestUser(authUser)} />}
      {view === "bookings" && (isGuestUser(authUser) ? <MyBookings bookings={bookings} navigate={navigate} onCancelBooking={cancelBooking} /> : <AccessPanel title="Guest account required" text="Bookings are attached to guest accounts. Sign in as a guest to view reservations." action="Sign In" onAction={() => openAuth("login")} />)}
      {view === "manager" && (isManagerUser(authUser) ? <AdminDashboard authUser={authUser} request={request} roleLabel={roleLabel} /> : <AccessPanel title="Manager access required" text="The hotel operations console is visible only to hotel managers and Roomly admins." action="Sign In" onAction={() => openAuth("login")} />)}
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
      {cancelTarget && (
        <ConfirmDialog
          booking={cancelTarget}
          loading={loading}
          onClose={() => setCancelTarget(null)}
          onConfirm={confirmCancelBooking}
        />
      )}
      {toast && <Toast message={toast} />}
      {view !== "manager" && <Footer authUser={authUser} navigate={navigate} openAuth={openAuth} searchHotels={searchHotels} />}
    </div>
  );
}

function Navbar({ view, navigate, menuOpen, setMenuOpen, searchHotels, authUser, openAuth, logout }) {
  const links = [
    ["hotels", "Hotels"],
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

function AuthPage({ mode, setMode, onSubmit }) {
  const isSignup = mode === "signup";
  const [loginForm, setLoginForm] = React.useState({
    email: "",
    password: ""
  });
  const [signupForm, setSignupForm] = React.useState({
    username: "",
    email: "",
    password: "",
    role: "USER"
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
            <>
              <div className="account-intent">
                <span>What do you want to do?</span>
                <div>
                  <label className={form.role === "USER" ? "active" : ""}>
                    <input
                      checked={form.role === "USER"}
                      name="role"
                      onChange={update}
                      type="radio"
                      value="USER"
                    />
                    <strong>I want to book a hotel</strong>
                    <small>Guest account for stays and reservations.</small>
                  </label>
                  <label className={form.role === "HOTEL_MANAGER" ? "active" : ""}>
                    <input
                      checked={form.role === "HOTEL_MANAGER"}
                      name="role"
                      onChange={update}
                      type="radio"
                      value="HOTEL_MANAGER"
                    />
                    <strong>I want to manage a property</strong>
                    <small>Manager account for hotel operations.</small>
                  </label>
                </div>
              </div>

              <Field icon={UserRound} label="Username">
                <input
                  name="username"
                  value={form.username}
                  onChange={update}
                  placeholder="aamir_roomly"
                  required
                />
              </Field>
            </>
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
            <span>{isSignup ? "Choose a guest account for bookings or a manager account for property operations." : "Access tokens are stored locally for protected API calls."}</span>
          </div>
        </form>
      </section>
    </main>
  );
}

function Toast({ message }) {
  return <div className="toast">{message}</div>;
}

function ConfirmDialog({ booking, loading, onClose, onConfirm }) {
  return (
    <div className="confirm-overlay" role="dialog" aria-modal="true" aria-labelledby="cancel-booking-title">
      <div className="checkout-backdrop" onClick={loading ? undefined : onClose} />
      <section className="confirm-dialog">
        <button className="modal-close" onClick={onClose} disabled={loading} type="button"><X size={20} /></button>
        <span className="confirm-kicker">Cancel reservation</span>
        <h2 id="cancel-booking-title">Release this booking?</h2>
        <p>
          This will cancel booking #{booking.id} and return the reserved rooms to inventory.
        </p>
        <div className="confirm-summary">
          <span>{booking.hotelName || "Roomly Stay"}</span>
          <strong>{booking.checkInDate} - {booking.checkoutDate}</strong>
        </div>
        <div className="confirm-actions">
          <button className="soft-button" onClick={onClose} disabled={loading} type="button">Keep Booking</button>
          <button className="danger-button solid" onClick={onConfirm} disabled={loading} type="button">
            {loading ? "Cancelling..." : "Cancel Booking"}
          </button>
        </div>
      </section>
    </div>
  );
}

function Footer({ authUser, navigate, openAuth, searchHotels }) {
  const columns = [
    [
      "Explore",
      { label: "Home", action: () => navigate("home") },
      { label: "Live Hotels", action: searchHotels },
      { label: "Book Now", action: searchHotels },
      { label: "Sign In", action: () => openAuth("login") }
    ],
    ...(isManagerUser(authUser)
      ? [[
          "For Managers",
          { label: "Manager Console", action: () => navigate("manager") },
          { label: "Browse Hotels", action: searchHotels },
          { label: "Home", action: () => navigate("home") },
          { label: "Sign Out From Navbar", action: () => window.scrollTo({ top: 0, behavior: "smooth" }) }
        ]]
      : [[
          "For Guests",
          { label: "Collections", action: searchHotels },
          { label: "My Bookings", action: () => (authUser ? navigate("bookings") : openAuth("login")) },
          { label: "Create Account", action: () => openAuth("signup") },
          { label: "Support", action: () => window.location.assign("mailto:support@roomly.dev") }
        ]])
  ];

  return (
    <footer className="footer">
      <div>
        <h2>Roomly</h2>
        <p>Elevating the art of the stay through meticulous curation and live pricing.</p>
      </div>
      {columns.map(([title, ...items]) => (
        <div key={title}>
          <h3>{title}</h3>
          {items.map((item) => (
            <button key={item.label} onClick={item.action} type="button">
              {item.label}
            </button>
          ))}
        </div>
      ))}
    </footer>
  );
}

createRoot(document.getElementById("root")).render(<App />);
