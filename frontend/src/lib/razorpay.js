export function loadRazorpayCheckout() {
  if (window.Razorpay) return Promise.resolve(true);

  return new Promise((resolve) => {
    const existingScript = document.querySelector('script[src="https://checkout.razorpay.com/v1/checkout.js"]');
    if (existingScript) {
      existingScript.addEventListener("load", () => resolve(true), { once: true });
      existingScript.addEventListener("error", () => resolve(false), { once: true });
      return;
    }

    const script = document.createElement("script");
    script.src = "https://checkout.razorpay.com/v1/checkout.js";
    script.onload = () => resolve(true);
    script.onerror = () => resolve(false);
    document.body.appendChild(script);
  });
}

export function razorpayFailureMessage(response) {
  const error = response?.error;
  if (!error) return "Payment failed. Please try again.";
  const details = [error.code, error.source, error.step, error.reason]
    .filter(Boolean)
    .join(" / ");
  return details ? `${error.description || "Payment failed"} (${details})` : (error.description || "Payment failed. Please try again.");
}
