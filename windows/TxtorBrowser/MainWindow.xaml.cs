using System;
using System.Collections.ObjectModel;
using System.Linq;
using System.Windows;
using System.Windows.Input;
using Microsoft.Web.WebView2.Core;
using TxTorBrowser.Models;
using TxTorBrowser.Services;

namespace TxTorBrowser
{
    public partial class MainWindow : Window
    {
        public ObservableCollection<BrowserTab> Tabs { get; } = new ObservableCollection<BrowserTab>();
        public DownloadManager DownloadService { get; } = new DownloadManager();

        private BrowserTab? _activeTab;

        public MainWindow()
        {
            InitializeComponent();
            TabList.ItemsSource = Tabs;

            var defaultTab = new BrowserTab
            {
                Title = "Google",
                Url = "https://www.google.com"
            };
            Tabs.Add(defaultTab);
            _activeTab = defaultTab;

            InitializeWebView();
        }

        private async void InitializeWebView()
        {
            await WebViewControl.EnsureCoreWebView2Async(null);

            WebViewControl.CoreWebView2.SourceChanged += (s, e) =>
            {
                if (_activeTab != null)
                {
                    _activeTab.Url = WebViewControl.Source.ToString();
                    TxtUrl.Text = _activeTab.Url;
                }
            };

            WebViewControl.CoreWebView2.DocumentTitleChanged += (s, e) =>
            {
                if (_activeTab != null)
                {
                    _activeTab.Title = WebViewControl.CoreWebView2.DocumentTitle;
                }
            };

            WebViewControl.CoreWebView2.DownloadStarting += (s, e) =>
            {
                // Hook into native download service
                var uri = e.DownloadOperation.Uri;
                var suggestedName = System.IO.Path.GetFileName(e.ResultFilePath);
                _ = DownloadService.StartDownloadAsync(uri, suggestedName);
            };
        }

        private void BtnNewTab_Click(object sender, RoutedEventArgs e)
        {
            var newTab = new BrowserTab { Title = "New Tab", Url = "https://www.google.com" };
            Tabs.Add(newTab);
            _activeTab = newTab;
            WebViewControl.Source = new Uri(newTab.Url);
        }

        private void BtnCloseTab_Click(object sender, RoutedEventArgs e)
        {
            if (sender is FrameworkElement elem && elem.CommandParameter is string tabId)
            {
                var tab = Tabs.FirstOrDefault(t => t.Id == tabId);
                if (tab != null)
                {
                    Tabs.Remove(tab);
                    if (_activeTab?.Id == tabId && Tabs.Count > 0)
                    {
                        _activeTab = Tabs.Last();
                        WebViewControl.Source = new Uri(_activeTab.Url);
                    }
                }
            }
        }

        private void TxtUrl_KeyDown(object sender, KeyEventArgs e)
        {
            if (e.Key == Key.Enter)
            {
                var text = TxtUrl.Text.Trim();
                if (!string.IsNullOrWhiteSpace(text))
                {
                    var target = text.StartsWith("http://") || text.StartsWith("https://") 
                        ? text 
                        : $"https://www.google.com/search?q={Uri.EscapeDataString(text)}";
                    WebViewControl.Source = new Uri(target);
                }
            }
        }

        private void BtnBack_Click(object sender, RoutedEventArgs e)
        {
            if (WebViewControl.CanGoBack) WebViewControl.GoBack();
        }

        private void BtnForward_Click(object sender, RoutedEventArgs e)
        {
            if (WebViewControl.CanGoForward) WebViewControl.GoForward();
        }

        private void BtnReload_Click(object sender, RoutedEventArgs e)
        {
            WebViewControl.Reload();
        }

        private void BtnDesktopMode_Click(object sender, RoutedEventArgs e)
        {
            if (WebViewControl.CoreWebView2 != null && _activeTab != null)
            {
                _activeTab.IsDesktopMode = !_activeTab.IsDesktopMode;
                var ua = _activeTab.IsDesktopMode 
                    ? "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36"
                    : "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Mobile Safari/537.36";
                WebViewControl.CoreWebView2.Settings.UserAgent = ua;
                WebViewControl.Reload();
            }
        }

        private void BtnDownloads_Click(object sender, RoutedEventArgs e)
        {
            MessageBox.Show($"Active Downloads: {DownloadService.Tasks.Count(t => t.State == DownloadState.Downloading)}\nCompleted: {DownloadService.Tasks.Count(t => t.State == DownloadState.Completed)}", "TX TOR Download Manager");
        }
    }
}
