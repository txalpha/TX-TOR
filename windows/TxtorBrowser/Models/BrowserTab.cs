using System;
using System.ComponentModel;
using System.Runtime.CompilerServices;

namespace TxTorBrowser.Models
{
    public class BrowserTab : INotifyPropertyChanged
    {
        private string _id = Guid.NewGuid().ToString();
        private string _title = "New Tab";
        private string _url = "https://www.google.com";
        private bool _isLoading;
        private bool _isDesktopMode;
        private int _detectedMediaCount;

        public string Id => _id;

        public string Title
        {
            get => _title;
            set { _title = value; OnPropertyChanged(); }
        }

        public string Url
        {
            get => _url;
            set { _url = value; OnPropertyChanged(); }
        }

        public bool IsLoading
        {
            get => _isLoading;
            set { _isLoading = value; OnPropertyChanged(); }
        }

        public bool IsDesktopMode
        {
            get => _isDesktopMode;
            set { _isDesktopMode = value; OnPropertyChanged(); }
        }

        public int DetectedMediaCount
        {
            get => _detectedMediaCount;
            set { _detectedMediaCount = value; OnPropertyChanged(); }
        }

        public event PropertyChangedEventHandler? PropertyChanged;
        protected void OnPropertyChanged([CallerMemberName] string? name = null) =>
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(name));
    }
}
