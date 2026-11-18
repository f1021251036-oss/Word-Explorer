import { Component, type ErrorInfo, type ReactNode } from 'react';

interface AppErrorBoundaryProps {
  children: ReactNode;
}

interface AppErrorBoundaryState {
  hasError: boolean;
  errorMessage: string | null;
}

export class AppErrorBoundary extends Component<AppErrorBoundaryProps, AppErrorBoundaryState> {
  state: AppErrorBoundaryState = {
    hasError: false,
    errorMessage: null,
  };

  static getDerivedStateFromError(error: Error): AppErrorBoundaryState {
    return {
      hasError: true,
      errorMessage: error.message || 'Terjadi kesalahan pada tampilan aplikasi.',
    };
  }

  componentDidCatch(error: Error, info: ErrorInfo) {
    // Error details kept out of DOM except concise fallback message; surface in console only if needed
    void info;
    void error;
  }

  render() {
    if (this.state.hasError) {
      return (
        <div className="flow-page" role="alert">
          <main className="flow-container flow-container--centered">
            <div className="onboarding-card">
              <div className="onboarding-emblem" aria-hidden="true">
                <span>🛠️</span>
              </div>
              <h1 className="flow-heading">Terjadi Kendala Tampilan</h1>
              <p className="flow-subcopy">
                Ekspedisi mengalami galat tak terduga, namun progres belajarmu tetap tersimpan aman
                di peramban.
              </p>
              {this.state.errorMessage && (
                <p className="auth-muted-copy" style={{ fontSize: '0.82rem' }}>
                  Detail: {this.state.errorMessage}
                </p>
              )}
              <div className="flow-footer-actions" style={{ width: '100%' }}>
                <button
                  type="button"
                  className="btn btn--primary"
                  onClick={() => window.location.reload()}
                >
                  Muat Ulang Halaman
                </button>
                <button
                  type="button"
                  className="btn btn--secondary"
                  onClick={() => {
                    this.setState({ hasError: false, errorMessage: null });
                    window.location.assign('/map');
                  }}
                >
                  Kembali ke Peta
                </button>
              </div>
            </div>
          </main>
        </div>
      );
    }

    return this.props.children;
  }
}
