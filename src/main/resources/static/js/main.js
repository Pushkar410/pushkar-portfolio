// ==========================================================================
// Pushkar R Adiga — Interactive Portfolio Script
// ==========================================================================

document.addEventListener('DOMContentLoaded', () => {

    // 1. Mobile Menu Toggle
    const menuToggle = document.getElementById('menuToggle');
    const navMenu = document.getElementById('navMenu');

    if (menuToggle && navMenu) {
        menuToggle.addEventListener('click', () => {
            const isOpen = navMenu.classList.toggle('open');
            menuToggle.classList.toggle('open', isOpen);
            menuToggle.setAttribute('aria-expanded', String(isOpen));
        });

        // Close drawer when any nav link is clicked
        navMenu.querySelectorAll('a').forEach(link => {
            link.addEventListener('click', () => {
                navMenu.classList.remove('open');
                menuToggle.classList.remove('open');
                menuToggle.setAttribute('aria-expanded', 'false');
            });
        });
    }

    // 2. Navbar Scroll Styling
    const topNav = document.getElementById('topNav');
    const handleScroll = () => {
        if (!topNav) return;
        if (window.scrollY > 40) {
            topNav.classList.add('scrolled');
        } else {
            topNav.classList.remove('scrolled');
        }
    };
    window.addEventListener('scroll', handleScroll, { passive: true });
    handleScroll();

    // 3. Scroll Spy for Active Navigation
    const sections = document.querySelectorAll('section[id]');
    const navLinks = document.querySelectorAll('.nav-menu .nav-link');

    const updateActiveNav = () => {
        const scrollY = window.pageYOffset;
        sections.forEach(section => {
            const sectionHeight = section.offsetHeight;
            const sectionTop = section.offsetTop - 140;
            const sectionId = section.getAttribute('id');

            if (scrollY > sectionTop && scrollY <= sectionTop + sectionHeight) {
                navLinks.forEach(link => {
                    const href = link.getAttribute('href');
                    if (href === `#${sectionId}`) {
                        link.classList.add('active');
                    } else {
                        link.classList.remove('active');
                    }
                });
            }
        });
    };
    window.addEventListener('scroll', updateActiveNav, { passive: true });
    updateActiveNav();

    // 4. Live IST Clock (Bengaluru Time)
    const istClockElement = document.getElementById('istClock');
    const updateISTClock = () => {
        if (!istClockElement) return;
        try {
            const now = new Date();
            const timeFormatter = new Intl.DateTimeFormat('en-GB', {
                timeZone: 'Asia/Kolkata',
                hour: '2-digit',
                minute: '2-digit',
                second: '2-digit',
                hour12: false
            });
            istClockElement.textContent = `BENGALURU: ${timeFormatter.format(now)} IST`;
        } catch (e) {
            // Fallback
            istClockElement.textContent = 'BENGALURU, INDIA';
        }
    };
    setInterval(updateISTClock, 1000);
    updateISTClock();

    // 5. One-Click Copy Email with Toast Notification
    const toast = document.getElementById('toastNotification');
    const toastMessage = document.getElementById('toastMessage');
    let toastTimeout = null;

    const showToast = (message) => {
        if (!toast) return;
        if (toastMessage) toastMessage.textContent = message;
        toast.classList.add('show');
        clearTimeout(toastTimeout);
        toastTimeout = setTimeout(() => {
            toast.classList.remove('show');
        }, 3200);
    };

    document.querySelectorAll('.email-copy-trigger').forEach(trigger => {
        trigger.addEventListener('click', async (e) => {
            e.preventDefault();
            const email = trigger.getAttribute('data-email') || 'pushkar.1si24is072@gmail.com';
            try {
                if (navigator.clipboard && window.isSecureContext) {
                    await navigator.clipboard.writeText(email);
                } else {
                    // Fallback for older browsers
                    const tempInput = document.createElement('input');
                    tempInput.value = email;
                    document.body.appendChild(tempInput);
                    tempInput.select();
                    document.execCommand('copy');
                    document.body.removeChild(tempInput);
                }
                showToast(`Copied ${email} to clipboard!`);
            } catch (err) {
                // If copying is blocked, open mailto
                window.location.href = `mailto:${email}`;
            }
        });
    });

    // 6. Contact Form Submission Handling
    const contactForm = document.getElementById('contactForm');
    const formStatus = document.getElementById('formStatus');
    const submitBtn = document.getElementById('submitBtn');

    if (contactForm) {
        contactForm.addEventListener('submit', async (e) => {
            e.preventDefault();

            const nameInput = contactForm.querySelector('input[name="name"]');
            const emailInput = contactForm.querySelector('input[name="email"]');
            const messageInput = contactForm.querySelector('textarea[name="message"]');

            const name = nameInput ? nameInput.value.trim() : '';
            const email = emailInput ? emailInput.value.trim() : '';
            const message = messageInput ? messageInput.value.trim() : '';

            if (!name || !email || !message) {
                if (formStatus) {
                    formStatus.className = 'form-status error';
                    formStatus.textContent = 'Please fill out all required fields.';
                }
                return;
            }

            // Set loading state
            if (submitBtn) {
                submitBtn.disabled = true;
                submitBtn.innerHTML = 'Sending...';
            }
            if (formStatus) {
                formStatus.className = 'form-status';
                formStatus.textContent = '';
            }

            try {
                const response = await fetch('/api/contact', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Accept': 'application/json'
                    },
                    body: JSON.stringify({ name, email, message })
                });

                const result = await response.json();

                if (!response.ok) {
                    throw new Error(result.message || 'Could not send message. Please reach out via email.');
                }

                if (formStatus) {
                    formStatus.className = 'form-status success';
                    formStatus.textContent = result.message || 'Thank you! Your message has been sent.';
                }
                contactForm.reset();
            } catch (error) {
                if (formStatus) {
                    formStatus.className = 'form-status error';
                    formStatus.textContent = error.message || 'Something went wrong. Please email directly.';
                }
            } finally {
                if (submitBtn) {
                    submitBtn.disabled = false;
                    submitBtn.innerHTML = '<span>Send Message</span><span class="arrow-up-right">↗</span>';
                }
            }
        });
    }
});
