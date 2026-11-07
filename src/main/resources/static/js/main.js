// Motion and the contact form. The page is fully readable without any of this.

// ---- Motion -------------------------------------------------------------
(() => {
  const reduced = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
  if (reduced || !('IntersectionObserver' in window)) return;

  // Reveal blocks as they enter the viewport, staggering siblings.
  const targets = document.querySelectorAll(
    '.section-title, .prose p, .facts, .project, .row, .entry, ' +
    '.contact-lead, .contact-email, .elsewhere, .form'
  );
  const counters = new Map();
  targets.forEach((el) => {
    const parent = el.parentElement;
    const n = counters.get(parent) || 0;
    counters.set(parent, n + 1);
    el.style.setProperty('--d', `${Math.min(n, 5) * 90}ms`);
    el.setAttribute('data-reveal', '');
  });

  const reveal = new IntersectionObserver((entries) => {
    entries.forEach((entry) => {
      if (!entry.isIntersecting) return;
      entry.target.classList.add('in');
      reveal.unobserve(entry.target);
    });
  }, { rootMargin: '0px 0px -8% 0px', threshold: 0.08 });
  document.querySelectorAll('[data-reveal]').forEach((el) => reveal.observe(el));

  // Section rules draw in.
  const rules = new IntersectionObserver((entries) => {
    entries.forEach((entry) => {
      if (!entry.isIntersecting) return;
      entry.target.classList.add('in-rule');
      rules.unobserve(entry.target);
    });
  }, { rootMargin: '0px 0px -12% 0px', threshold: 0 });
  document.querySelectorAll('.section').forEach((el) => {
    el.setAttribute('data-rule', '');
    rules.observe(el);
  });

  // Active section in the nav.
  const links = new Map();
  document.querySelectorAll('.site-nav a[href^="#"]').forEach((a) => links.set(a.getAttribute('href').slice(1), a));
  const spy = new IntersectionObserver((entries) => {
    entries.forEach((entry) => {
      if (!entry.isIntersecting) return;
      links.forEach((a, id) => {
        if (id === entry.target.id) a.setAttribute('aria-current', 'true');
        else a.removeAttribute('aria-current');
      });
    });
  }, { rootMargin: '-35% 0px -55% 0px', threshold: 0 });
  links.forEach((_, id) => { const s = document.getElementById(id); if (s) spy.observe(s); });

  // Reading progress line.
  const bar = document.createElement('div');
  bar.className = 'progress';
  bar.setAttribute('aria-hidden', 'true');
  document.body.prepend(bar);
  let ticking = false;
  const update = () => {
    const max = document.documentElement.scrollHeight - window.innerHeight;
    bar.style.setProperty('--p', max > 0 ? Math.min(window.scrollY / max, 1).toFixed(4) : 0);
    ticking = false;
  };
  window.addEventListener('scroll', () => {
    if (!ticking) { ticking = true; requestAnimationFrame(update); }
  }, { passive: true });
  update();
})();

// ---- Contact form -------------------------------------------------------
(() => {
  const form = document.getElementById('contact-form');
  if (!form) return;

  const status = document.getElementById('form-status');
  const button = form.querySelector('button[type="submit"]');
  const fields = ['name', 'email', 'message'];

  const setError = (field, text) => {
    const input = form.elements[field];
    const out = document.getElementById(`f-${field}-error`);
    if (!input || !out) return;
    out.textContent = text || '';
    if (text) input.setAttribute('aria-invalid', 'true');
    else input.removeAttribute('aria-invalid');
  };

  const setStatus = (text, state) => {
    status.textContent = text;
    if (state) status.dataset.state = state;
    else delete status.dataset.state;
  };

  const clientValidate = () => {
    const problems = {};
    if (!form.elements.name.value.trim()) problems.name = 'Enter your name.';
    const email = form.elements.email.value.trim();
    if (!email) problems.email = 'Enter your email so I can reply.';
    else if (!form.elements.email.checkValidity()) problems.email = 'That email address does not look right.';
    if (!form.elements.message.value.trim()) problems.message = 'Write a message.';
    return problems;
  };

  form.addEventListener('submit', async (event) => {
    event.preventDefault();
    fields.forEach((f) => setError(f, ''));
    setStatus('');

    const problems = clientValidate();
    const firstBad = fields.find((f) => problems[f]);
    if (firstBad) {
      fields.forEach((f) => setError(f, problems[f]));
      form.elements[firstBad].focus();
      return;
    }

    button.disabled = true;
    setStatus('Sending…');

    try {
      const response = await fetch(form.dataset.endpoint, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
        body: JSON.stringify({
          name: form.elements.name.value.trim(),
          email: form.elements.email.value.trim(),
          message: form.elements.message.value.trim(),
          website: form.elements.website.value
        })
      });

      let body = {};
      try { body = await response.json(); } catch (_) { /* non-JSON error body */ }

      if (response.status === 201) {
        form.reset();
        setStatus(body.message || 'Message saved. I will reply by email.', 'ok');
      } else if (response.status === 400 && body.errors) {
        fields.forEach((f) => setError(f, body.errors[f]));
        const bad = fields.find((f) => body.errors[f]);
        if (bad) form.elements[bad].focus();
        setStatus('Check the highlighted fields.', 'error');
      } else {
        setStatus(body.message || 'The message was not sent. Please email me instead.', 'error');
      }
    } catch (_) {
      setStatus('Could not reach the server. Please email me instead.', 'error');
    } finally {
      button.disabled = false;
    }
  });
})();
