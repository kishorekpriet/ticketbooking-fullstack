import { useState, useEffect } from 'react';
import './App.css';
import Auth from './Auth';

function App() {
  const [showBookingPage, setShowBookingPage] = useState(false);
  const [selectedSeats, setSelectedSeats] = useState([]);
  const [bookedSeats, setBookedSeats] = useState([]); 
  
  const [isLoggedIn, setIsLoggedIn] = useState(!!localStorage.getItem('token'));
  const [showAuth, setShowAuth] = useState(false);
  
  const totalSeats = Array.from({ length: 20 }, (_, i) => i + 1);

  // 1. Existing seat fetcher
  useEffect(() => {
    if (showBookingPage) {
      fetchSeats();
    }
  }, [showBookingPage]);

  // 🌟 STRIPE ADDITION 1: Catch the user when they return from the Stripe Payment Page!
  useEffect(() => {
    const query = new URLSearchParams(window.location.search);
    const paymentStatus = query.get("payment");

    if (paymentStatus === "success") {
      const pendingSeats = JSON.parse(localStorage.getItem('pendingSeats')) || [];
      const token = localStorage.getItem('token');
      const userEmail = localStorage.getItem('userEmail');

      if (pendingSeats.length > 0 && token) {
        // Now that Stripe got the money, actually book the seats in MySQL!
        const bookSeatsInDB = async () => {
          for (const seatId of pendingSeats) {
            await fetch(`http://localhost:8081/api/tickets/book?movieId=1&seatId=${seatId}&userEmail=${userEmail}`, {
              method: 'POST',
              headers: { 'Authorization': `Bearer ${token}` }
            });
          }
          alert("✅ Payment Successful! Your VIP tickets are locked in.");
          localStorage.removeItem('pendingSeats'); 
          setSelectedSeats([]); 
          fetchSeats(); 
          
          window.history.replaceState(null, '', window.location.pathname);
        };
        
        bookSeatsInDB();
      }
    }

    if (paymentStatus === "cancelled") {
      alert("❌ Payment Cancelled. You were not charged.");
      localStorage.removeItem('pendingSeats');
      window.history.replaceState(null, '', window.location.pathname);
    }
  }, []); 

  const fetchSeats = async () => {
    try {
      const response = await fetch('http://localhost:8081/api/seats');
      const data = await response.json();
      
      const takenSeatIds = [];
      data.forEach(seat => {
        if (seat.booked === true || seat.isBooked === true) {
          takenSeatIds.push(seat.id);
        }
      });
        
      setBookedSeats(takenSeatIds);
    } catch (error) {
      console.error("❌ Error fetching seats:", error);
    }
  };

  const toggleSeat = (seatId) => {
    if (bookedSeats.includes(seatId)) return;

    if (selectedSeats.includes(seatId)) {
      setSelectedSeats(selectedSeats.filter(id => id !== seatId));
    } else {
      setSelectedSeats([...selectedSeats, seatId]);
    }
  };

  const calculateTotal = () => {
    let total = 0;
    selectedSeats.forEach(seat => {
      if (seat <= 5) total += 250; 
      else total += 150; 
    });
    return total;
  };

  // 🌟 STRIPE ADDITION 2: The Departure (Teleporting the user to Stripe)
  const handlePayment = async () => {
    const token = localStorage.getItem('token');

    if (!token) {
      alert("You must be logged in to book tickets!");
      setShowAuth(true);
      return;
    }

    // Save the seats to memory before leaving the page!
    localStorage.setItem('pendingSeats', JSON.stringify(selectedSeats));
    const totalAmount = calculateTotal();

    try {
      const response = await fetch('http://localhost:8081/api/payment/create-checkout-session', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}` 
        },
        body: JSON.stringify({ amount: totalAmount }) 
      });

      if (response.ok) {
        const data = await response.json();
        // Teleport to the secure Stripe Checkout page!
        window.location.href = data.checkoutUrl;
      } else {
        const errorText = await response.text();
        alert(`Payment initiation failed: ${errorText}`);
      }
    } catch (error) {
      alert("Error connecting to the server. Is Spring Boot running?");
    }
  };

  return (
    <div className="entry-page">
      
      <div className="header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h2>BookYourTicket Kishore</h2>
          <p>Coimbatore | English</p>
        </div>

        {isLoggedIn ? (
          <button onClick={() => {
            localStorage.removeItem('token');
            localStorage.removeItem('userEmail');
            setIsLoggedIn(false);
          }} style={{ background: 'transparent', color: 'white', border: '1px solid white', padding: '5px 15px', borderRadius: '4px', cursor: 'pointer' }}>
            Logout
          </button>
        ) : (
          <button onClick={() => setShowAuth(true)} style={{ background: '#f84464', color: 'white', border: 'none', padding: '5px 15px', borderRadius: '4px', cursor: 'pointer' }}>
            Sign In
          </button>
        )}
      </div>

      {showAuth ? (
        <Auth onLoginSuccess={() => {
          setIsLoggedIn(true);
          setShowAuth(false);
        }} />
      ) : showBookingPage ? (
        <div>
          <div className="seat-map-container">
            <div className="category-title">Premium - Rs. 250.00</div>
            <div className="seat-row">
              <div className="row-label">A</div>
              {totalSeats.slice(0, 5).map(seatId => (
                <div 
                  key={seatId}
                  className={`seat ${selectedSeats.includes(seatId) ? 'selected' : ''} ${bookedSeats.includes(seatId) ? 'booked' : ''}`}
                  onClick={() => toggleSeat(seatId)}
                >{seatId}</div>
              ))}
            </div>

            <div className="category-title" style={{ marginTop: '30px' }}>Standard - Rs. 150.00</div>
            {['B', 'C', 'D'].map((rowLetter, rowIndex) => (
              <div className="seat-row" key={rowLetter}>
                <div className="row-label">{rowLetter}</div>
                {totalSeats.slice(5 + (rowIndex * 5), 10 + (rowIndex * 5)).map(seatId => (
                  <div 
                    key={seatId}
                    className={`seat ${selectedSeats.includes(seatId) ? 'selected' : ''} ${bookedSeats.includes(seatId) ? 'booked' : ''}`}
                    onClick={() => toggleSeat(seatId)}
                  >{seatId}</div>
                ))}
              </div>
            ))}

            <div className="screen-container">
              <div className="screen"></div>
              <div className="screen-text">All eyes this way please!</div>
            </div>
          </div>

          {selectedSeats.length > 0 && (
            <div className="footer">
              <button className="pay-btn" onClick={handlePayment}>
                Pay Rs. {calculateTotal()}.00
              </button>
            </div>
          )}
        </div>
      ) : (
        <div className="hero-banner">
          <div className="hero-content">
            <h1 className="movie-title">Inception (U/A)</h1>
            <div className="movie-tags">
              <span className="tag">Action</span>
              <span className="tag">Sci-Fi</span>
              <span className="tag">Thriller</span>
              <span className="tag">2h 28m</span>
            </div>
            <p className="movie-description">
              A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O., but his tragic past may doom the project and his team to disaster.
            </p>
            <button className="book-now-btn" onClick={() => setShowBookingPage(true)}>
              Book Tickets
            </button>
          </div>
        </div>
      )}
    </div>
  );
}

export default App;