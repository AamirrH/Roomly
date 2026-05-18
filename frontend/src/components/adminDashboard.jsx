import React from "react";
import {
  BadgeIndianRupee,
  BedDouble,
  Bell,
  Building2,
  CalendarDays,
  Check,
  CircleDollarSign,
  Download,
  Edit3,
  Hotel,
  LayoutDashboard,
  LockKeyhole,
  MapPin,
  Plus,
  Search,
  SlidersHorizontal,
  Trash2,
  UserRound,
  Users
} from "lucide-react";
import { sampleBookings } from "../data/roomlyContent";
import { csvToList, displayHotelName, listToCsv, money } from "../lib/display";
import { StatusBadge } from "./stayExperience";

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

export function AdminDashboard({ authUser, request, roleLabel }) {
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
          {tab === "hotels" && <AdminHotels hotels={hotels} refresh={loadHotels} showMessage={showMessage} request={request} />}
          {tab === "rooms" && <AdminRooms hotelId={selectedHotelId} rooms={rooms} refresh={() => loadRooms(selectedHotelId)} showMessage={showMessage} request={request} />}
          {tab === "bookings" && (
            <AdminBookings
              bookings={managerBookings}
              filters={bookingFilters}
              setFilters={setBookingFilters}
              loadBookings={() => loadBookings(selectedHotelId, bookingFilters)}
              selectedHotel={selectedHotel}
            />
          )}
          {tab === "revenue" && <AdminRevenue request={request} />}
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

function AdminHotels({ hotels, refresh, showMessage, request }) {
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

function AdminRooms({ hotelId, rooms, refresh, showMessage, request }) {
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

function AdminRevenue({ request }) {
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
